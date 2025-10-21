package online;

import java.util.*;
import java.util.stream.*;

/**
 * OCR 排序与分段（段落聚类）工具
 * 假设坐标单位统一（像素/点皆可），(x,y) 为左上角，w,h 为宽高。
 * 排序规则：先按 y（行顶）排序，行内再按 x 排序。
 * 段落分组：基于行间距与行内块的密度/间隙进行启发式聚类。
 */
public class OcrLayoutGrouper {

    // ===== 数据结构 =====
    public static final class Box {
        public final String text;
        public final double x, y, w, h;

        public Box(String text, double x, double y, double w, double h) {
            this.text = text == null ? "" : text;
            this.x = x; this.y = y; this.w = w; this.h = h;
        }

        public double right() { return x + w; }
        public double bottom() { return y + h; }

        @Override public String toString() {
            return String.format("Box{text='%s', x=%.1f, y=%.1f, w=%.1f, h=%.1f}", shortText(text), x, y, w, h);
        }
        private static String shortText(String s) {
            s = s.replace("\n"," ").trim();
            return s.length() > 20 ? s.substring(0,20) + "…" : s;
        }
    }

    public static final class Line {
        public final List<Box> boxes = new ArrayList<>();
        public double x, y, w, h;
        public void add(Box b) { boxes.add(b); }
        public void finalizeGeometry() {
            x = boxes.stream().mapToDouble(b -> b.x).min().orElse(0);
            y = boxes.stream().mapToDouble(b -> b.y).min().orElse(0);
            double right = boxes.stream().mapToDouble(Box::right).max().orElse(0);
            double bottom = boxes.stream().mapToDouble(Box::bottom).max().orElse(0);
            w = Math.max(0, right - x);
            h = Math.max(0, bottom - y);
        }
        public String joinedText(String sep) {
            return boxes.stream().map(b -> b.text).collect(Collectors.joining(sep));
        }
        @Override public String toString() {
            return "Line{" + joinedText(" | ") + "}";
        }
    }

    public static final class Paragraph {
        public final List<Line> lines = new ArrayList<>();
        public double x, y, w, h;
        public void add(Line ln) { lines.add(ln); }
        public void finalizeGeometry() {
            x = lines.stream().mapToDouble(l -> l.x).min().orElse(0);
            y = lines.stream().mapToDouble(l -> l.y).min().orElse(0);
            double right = lines.stream().mapToDouble(l -> l.x + l.w).max().orElse(0);
            double bottom = lines.stream().mapToDouble(l -> l.y + l.h).max().orElse(0);
            w = Math.max(0, right - x);
            h = Math.max(0, bottom - y);
        }
        public String text() { return lines.stream().map(l -> l.joinedText(" ")).collect(Collectors.joining("\n")); }
        @Override public String toString() { return "Paragraph{\n" + text() + "\n}"; }
    }

    // ===== 参数（可按你的数据调节）=====
    public static final class Params {
        /** 判断“同一行”的垂直重叠阈值：两 box 的垂直重叠 / 较小高度 >= 该阈值，则视作同一行 */
        public double verticalOverlapForSameLine = 0.6;
        /** 行内合并文本时的分隔符 */
        public String intraLineSeparator = " ";
        /** 用于统计的“典型行高”取法：使用 box 高度的中位数乘以系数 */
        public double typicalLineHeightScale = 1.0;
        /** 新段落阈值：当前行与上一行的行距 > k * 典型行高，则断段 */
        public double newParagraphGapK = 0.8;
        /** 左边缩进（相对上一行左边界）超过此比例（相对典型行高），也可能触发新段 */
        public double indentTriggerK = 0.6;
        /** 行内空隙过大（相对该行平均字符宽的倍数）时，认为有列/块边界，可弱化合并 */
        public double largeGapCharWidthK = 4.0;
        /** 估计字符宽：用 box 宽 / 文本长度 的中位数 */
        public boolean breakLineOnLargeInlineGap = false; // 简化：默认不在行内切分
    }

    // ===== 主流程 =====
    public static List<Paragraph> sortAndGroup(List<Box> rawBoxes, Params p) {
        if (rawBoxes == null || rawBoxes.isEmpty()) return Collections.emptyList();

        // 1) 预清洗：去掉空文本或空尺寸
        List<Box> boxes = rawBoxes.stream()
                .filter(b -> b != null && b.w > 0 && b.h > 0 && b.text != null && !b.text.trim().isEmpty())
                .collect(Collectors.toList());
        if (boxes.isEmpty()) return Collections.emptyList();

        // 2) 基本排序：先 y 再 x（稳定排序）
        boxes.sort(Comparator.<Box>comparingDouble(b -> b.y)
                .thenComparingDouble(b -> b.x));

        // 3) 统计典型行高与字符宽，便于做自适应阈值
        double typicalLineHeight = median(boxes.stream().mapToDouble(b -> b.h).toArray()) * p.typicalLineHeightScale;
        double typicalCharWidth = estimateTypicalCharWidth(boxes);

        // 4) 分行：按“垂直重叠”聚类到行
        List<Line> lines = groupToLines(boxes, p.verticalOverlapForSameLine);

        // 行内按 x 排序，行几何收敛
        for (Line ln : lines) {
            ln.boxes.sort(Comparator.comparingDouble(b -> b.x));
            if (p.breakLineOnLargeInlineGap && typicalCharWidth > 0) {
                // 可选：遇到大空隙时拆分为多行（列/公式/表格时可能有用）
                // 这里保留扩展点：如需要，再实现行内切分
            }
            ln.finalizeGeometry();
        }

        // 5) 分段：根据行距 + 缩进 进行启发式断段
        List<Paragraph> paras = groupToParagraphs(lines, typicalLineHeight, typicalCharWidth, p);

        // 段落几何收敛
        for (Paragraph pg : paras) pg.finalizeGeometry();

        return paras;
    }

    // ===== 工具实现 =====

    private static List<Line> groupToLines(List<Box> boxes, double overlapThresh) {
        List<Line> lines = new ArrayList<>();
        for (Box b : boxes) {
            Line hit = null;
            double bestOverlap = 0;
            for (Line ln : lines) {
                // 与该行的“代表区域”做重叠判断：用行当前的 y~y+h；初期可用行内第一 box
                // 这里用行内所有 box 的 y 范围来近似
                double lnTop = ln.boxes.stream().mapToDouble(bb -> bb.y).min().orElse(b.y);
                double lnBottom = ln.boxes.stream().mapToDouble(bb -> bb.bottom()).max().orElse(b.bottom());
                double inter = overlap1D(b.y, b.bottom(), lnTop, lnBottom);
                double minH = Math.min(b.h, Math.max(1e-6, lnBottom - lnTop));
                double overlap = inter / minH;
                if (overlap >= overlapThresh && overlap > bestOverlap) {
                    hit = ln; bestOverlap = overlap;
                }
            }
            if (hit == null) {
                hit = new Line();
                lines.add(hit);
            }
            hit.add(b);
        }
        // 行按 y 排序
        lines.sort(Comparator.comparingDouble(l -> l.boxes.stream().mapToDouble(bb -> bb.y).min().orElse(0)));
        return lines;
    }

    private static List<Paragraph> groupToParagraphs(List<Line> lines, double typicalLineHeight, double typicalCharWidth, Params p) {
        List<Paragraph> paras = new ArrayList<>();
        Paragraph cur = null;

        for (int i = 0; i < lines.size(); i++) {
            Line ln = lines.get(i);
            if (cur == null) {
                cur = new Paragraph(); cur.add(ln);
                continue;
            }
            Line prev = lines.get(i - 1);

            double lineGap = ln.y - (prev.y + prev.h); // 上一行底到本行顶的间距
            boolean bigVerticalGap = lineGap > p.newParagraphGapK * typicalLineHeight;

            double indent = Math.max(0, ln.x - prev.x);
            boolean strongIndent = indent > p.indentTriggerK * typicalLineHeight;

            // 额外信号：上一行结尾是否有强终止符（句号、冒号、分号等）
            boolean prevEndsHard = endsWithPunct(prev.joinedText(p.intraLineSeparator));

            boolean newPara = bigVerticalGap || (strongIndent && !prevEndsHard);

            if (newPara) {
                cur.finalizeGeometry();
                paras.add(cur);
                cur = new Paragraph();
            }
            cur.add(ln);
        }
        if (cur != null) {
            cur.finalizeGeometry();
            paras.add(cur);
        }
        return paras;
    }

    private static boolean endsWithPunct(String s) {
        String t = s.trim();
        if (t.isEmpty()) return false;
        char c = t.charAt(t.length() - 1);
        // 可按语料再扩充
        return "。！!？?；;：:）)】]”.\"'》>、,".indexOf(c) >= 0;
    }

    private static double overlap1D(double a1, double a2, double b1, double b2) {
        double left = Math.max(a1, b1);
        double right = Math.min(a2, b2);
        return Math.max(0, right - left);
    }

    private static double median(double[] arr) {
        if (arr == null || arr.length == 0) return 0;
        double[] a = arr.clone();
        Arrays.sort(a);
        int n = a.length;
        return (n % 2 == 1) ? a[n/2] : 0.5*(a[n/2-1] + a[n/2]);
    }

    private static double estimateTypicalCharWidth(List<Box> boxes) {
        List<Double> widths = new ArrayList<>();
        for (Box b : boxes) {
            int len = b.text.replace("\n","").length();
            if (len >= 1) widths.add(b.w / len);
        }
        if (widths.isEmpty()) return 0;
        double[] arr = widths.stream().mapToDouble(d -> d).toArray();
        return median(arr);
    }

    // ======== Demo ========
    public static void main(String[] args) {
        // 这里用三行两段的假数据演示。你可以替换为你的 recBoxes+recText 列表。
        List<Box> input = Arrays.asList(
                new Box("标题：面试题", 100,  50, 300, 40),
                new Box("Java",         100, 120,  60, 24),
                new Box("算法",         170, 120,  60, 24),
                new Box("与",           235, 120,  30, 24),
                new Box("数据结构",     270, 120, 100, 24),

                new Box("请实现",       100, 160,  80, 22),
                new Box("从上到下",     185, 160,  90, 22),
                new Box("再从左到右",   280, 160, 110, 22),

                // 段落 2（有较大行距 + 缩进）
                new Box("进阶：",       120, 240,  60, 22),
                new Box("按聚集度分段", 185, 240, 110, 22),
                new Box("给出结果",     100, 280,  90, 22)
        );

        Params params = new Params();
        List<Paragraph> paragraphs = sortAndGroup(input, params);

        System.out.println("Paragraphs = " + paragraphs.size());
        for (int i = 0; i < paragraphs.size(); i++) {
            System.out.println("---- P" + (i+1) + " ----");
            Paragraph p = paragraphs.get(i);
            for (Line ln : p.lines) {
                System.out.println(ln.joinedText(" "));
            }
        }
    }
}
