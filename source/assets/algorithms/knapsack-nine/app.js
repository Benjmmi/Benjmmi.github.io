const chapters = [
  {
    id: 1,
    title: "01 背包",
    kicker: "每件物品只有一次机会。先学会把一个大问题，拆成“选”与“不选”两个更小的问题。",
    bridge: "这是整套课程的原点。后面八讲都会改变某个条件，但状态设计和“从子问题来”的思路始终不变。",
    duration: 8,
    sceneTitle: "重量 × 价值 × 容量：填出二维状态表",
    objectives: ["读懂二维表的行、列和格子", "从重量与价值推导每个新状态", "沿表格回溯最终选择方案"],
    formula: "f[i][v] = max(f[i-1][v], f[i-1][v-weight[i]] + value[i])",
    memory: "横轴是背包容量，纵轴是已经考虑的物品；每个格子只向上一行找答案。",
    type: "zero-one",
    steps: [
      ["先读懂表格", "例子里背包容量是 6。横向列出容量 0…6，纵向依次加入物品 A、B、C；f[i][v] 是对应格子的最大价值。"],
      ["放入物品 A", "A 的重量是 2、价值是 3。容量小于 2 时放不下；从容量 2 开始，选择 A 后价值都能达到 3。"],
      ["物品 B：比较两条路", "计算 f[2][5]：不选 B 得到上一行的 3；选择 B，则读取容量 5-3=2 的旧状态 3，再加 B 的价值 4，得到 7。"],
      ["物品 C：填完整张表", "同样逐格比较后，容量 6 的最优价值是 8。它来自上一行容量 2 的价值 3，再加 C 的价值 5。"],
      ["从答案反推物品", "从右下角 8 往回走：它不同于正上方的 7，所以选择 C；容量退到 2 后再选择 A。最终重量 2+4=6，价值 3+5=8。"],
    ],
    quiz: {
      q: "二维表中的 f[2][5] 表示什么？",
      options: ["前 2 件物品、容量 5 时的最大价值", "第 2 件物品的重量是 5", "容量 2 时必须得到价值 5"],
      answer: 0,
      explain: "行表示已经考虑的物品数量，列表示背包容量，格子保存这个子问题的最大价值。",
    },
  },
  {
    id: 2,
    title: "完全背包",
    kicker: "当同一种物品可以无限拿，方程看似更复杂，代码却只需要把循环方向反过来。",
    bridge: "上一讲用逆序阻止重复。本讲恰恰需要重复：让本轮刚更新的状态继续参与转移。",
    duration: 7,
    sceneTitle: "在二维表的同一行连续复用物品",
    objectives: ["看见同一种物品如何被重复选择", "理解选择分支为什么来自当前行", "从表格读懂容量顺序更新"],
    formula: "f[i][v] = max(f[i-1][v], f[i][v-weight[i]] + value[i])",
    memory: "01 背包选择时看上一行；完全背包选择时看当前行左侧，所以能继续拿同一种物品。",
    type: "complete",
    steps: [
      ["物品种类可以无限取", "背包容量仍是 6。物品 A 重量 2、价值 3，物品 B 重量 3、价值 4；每一种都有无限件。"],
      ["先拿一件 A", "计算 f[1][2]：不选 A 得 0；选择 A，则从当前行容量 0 的状态出发再加 3，得到价值 3。"],
      ["沿当前行再次拿 A", "计算 f[1][4] 时读取当前行 f[1][2]=3，再加一件 A 的价值 3，得到 6；容量 6 时还能继续得到 9。"],
      ["加入物品 B", "计算 f[2][5]：不选 B 得 6；选择 B，从当前行 f[2][2]=3 再加 4，得到 7。这对应一件 A 加一件 B。"],
      ["同一行来源就是关键", "表格从左向右填写，当前行左侧已经包含当前物品，所以可以继续复用。压成一维后，这正对应容量从小到大顺序枚举。"],
    ],
    quiz: {
      q: "完全背包选择第 i 种物品时，应该读取哪个状态？",
      options: ["当前行的 f[i][v-weight]", "上一行的 f[i-1][v-weight]", "下一行的 f[i+1][v]"],
      answer: 0,
      explain: "当前行已经允许使用第 i 种物品，再从当前行左侧转移，就能继续选择同一种物品。",
    },
  },
  {
    id: 3,
    title: "多重背包",
    kicker: "每种物品有数量上限。用 1、2、4……把数量拆开，就能重新回到熟悉的 01 背包。",
    bridge: "无限件和一件是两个极端。本讲处理它们之间的有限数量，并复用前两讲的过程。",
    duration: 9,
    sceneTitle: "宝物筛选：把 18 件库存捆成 5 组",
    objectives: ["看见朴素拆分为什么重复", "用二进制捆绑覆盖 0…18", "把每个捆绑组当成 01 物品"],
    formula: "18 = 1 + 2 + 4 + 8 + 3",
    memory: "数量拆成二进制组，每组当成一件 01 物品；O(n) 个选择压到 O(log n)。",
    type: "multiple",
    steps: [
      ["宝箱里有 18 件同类宝物", "每件宝物重量 1、价值 2，最多取 18 件。若拆成 18 个相同物品，会产生大量等价选择。"],
      ["先捆成 1、2、4 件", "每个捆绑包只能选一次。1、2、4 的子集已经能表示取 0…7 件。"],
      ["继续翻倍并补余数", "加入 8 件包后覆盖 0…15；剩余 3 件单独成包，得到 1、2、4、8、3。"],
      ["任意数量都能拼出来", "例如取 13 件就是选择 8+4+1；取 18 件则五包全选。不会遗漏，也不会超过库存。"],
      ["五个包进入 01 背包", "包的重量和价值同时乘包大小：8 件包就是重量 8、价值 16。18 个选择压缩成 5 个选择。"],
    ],
    quiz: {
      q: "18 件相同宝物的二进制捆绑可以怎样拆？",
      options: ["1, 2, 4, 8, 3", "1, 2, 4, 8, 16", "3, 6, 9"],
      answer: 0,
      explain: "1+2+4+8+3=18，这些包的子集能够表示 0 到 18 的每个数量。",
    },
  },
  {
    id: 4,
    title: "混合背包",
    kicker: "题目把三种物品混在一起时，不必发明新 DP：识别类型，再派发给对应的更新过程。",
    bridge: "前三讲已经准备好三个积木。本讲不增加新方程，而是练习识别、抽象和组合。",
    duration: 6,
    sceneTitle: "限时赏樱：一遍、无限遍与最多三遍",
    objectives: ["按物品类型选择更新过程", "掌握三种模板的统一接口", "体会抽象如何降低复杂度"],
    formula: "type → ZeroOnePack / CompletePack / MultiplePack",
    memory: "混合背包不是第四套算法，而是前三套算法的调度器。",
    type: "mixed",
    steps: [
      ["十分钟赏樱计划", "樱花 A 只能看一遍，B 可以反复看，C 最多看三遍；每次观赏都有耗时和美学值。"],
      ["A：一次限定", "A 每次耗时 3、价值 5，只能看一次，因此使用 01 背包的容量逆序更新。"],
      ["B：不限次数", "B 每次耗时 2、价值 3，可以反复看，因此使用完全背包的容量顺序更新。"],
      ["C：最多三次", "C 每次耗时 2、价值 4，最多看三次；拆成 1 次包和 2 次包，再按 01 背包处理。"],
      ["同一条时间轴统一结算", "三类观赏都更新同一个 f[0…10]。最终只需识别数量类型，再派发到对应更新过程。"],
    ],
    quiz: {
      q: "混合背包的核心设计是什么？",
      options: ["重新设计四维状态", "按类型调用已有过程", "先把所有物品变成完全背包"],
      answer: 1,
      explain: "识别类型并复用 ZeroOnePack、CompletePack、MultiplePack 即可。",
    },
  },
  {
    id: 5,
    title: "二维费用",
    kicker: "当一件物品同时消耗时间和金钱，容量不再是一条线，而是一张二维状态表。",
    bridge: "前四讲改变的是物品数量。本讲第一次改变约束：多一种独立限制，状态就多一维。",
    duration: 8,
    sceneTitle: "任务规划：同时守住时间与经费",
    objectives: ["把新增约束映射为状态维度", "写出二维费用转移", "理解“最多 M 件”也是第二维费用"],
    formula: "f[v][u] = max(f[v][u], f[v-a][u-b] + w)",
    memory: "每多一个必须同时满足的独立资源限制，通常就给状态增加一维。",
    type: "two-d",
    steps: [
      ["任务有两张价签", "预算只有 5 元、时间只有 5 分钟。每个任务同时消耗经费和时间，目标是完成尽可能多的任务。"],
      ["二维格子记录双重预算", "横轴是已花经费，纵轴是已用时间；f[money][time] 表示这两项限制下最多完成几个任务。"],
      ["加入任务 A", "任务 A 花 2 元、用 1 分钟。从 (0,0) 斜向移动到 (2,1)，完成数量从 0 变成 1。"],
      ["任务 B 与 C 继续扩散", "B 花 1 元用 3 分钟，C 花 3 元用 2 分钟。合法组合会在平面上留下可达路径。"],
      ["边界同时满足才合法", "A+B 到达 (3,4)，A+C 到达 (5,3)，都能完成 2 个任务；超出任一轴的组合都会被丢弃。"],
    ],
    quiz: {
      q: "题目增加“最多选 M 件”的限制，最自然的建模方式是？",
      options: ["给状态增加件数维", "把价值都除以 M", "只在答案处检查件数"],
      answer: 0,
      explain: "把每件物品的件数费用视为 1、总容量视为 M，就是标准二维费用。",
    },
  },
  {
    id: 6,
    title: "分组背包",
    kicker: "每组物品互相冲突，最多选一个。先枚举组，再枚举容量，最后比较组内候选。",
    bridge: "二维费用增加了资源限制；本讲增加选择关系：有些物品不能同时出现。",
    duration: 7,
    sceneTitle: "装备搭配：每个装备栏最多选一件",
    objectives: ["把互斥物品建模为组", "理解三层循环的正确顺序", "避免同组选择多个物品"],
    formula: "for group → for v: V→0 → for item in group",
    memory: "组在最外，容量在物品外：同一个容量状态只能从进入本组前的状态出发。",
    type: "group",
    steps: [
      ["背包只能承重 7", "武器、护甲、饰品各是一组；同一个装备栏最多穿戴一件，也可以整组不选。"],
      ["先从武器组挑一个", "木剑重 2 战力 3，长剑重 4 战力 6。两件互斥，只比较“不选、木剑、长剑”三种策略。"],
      ["再处理护甲组", "皮甲重 2 战力 2，铁甲重 4 战力 5。新状态只能从进入护甲组之前的结果转移。"],
      ["最后加入饰品组", "戒指重 1 战力 2，护符重 2 战力 3。容量 7 时，长剑+皮甲+戒指恰好装满，战力达到 10。"],
      ["循环顺序保护互斥关系", "组 → 容量逆序 → 组内候选。若先遍历候选，就可能错误地同时选中同组两件装备。"],
    ],
    quiz: {
      q: "分组背包的一维实现中，哪层循环必须在组内物品之外？",
      options: ["容量 v 的循环", "价值 w 的循环", "答案输出循环"],
      answer: 0,
      explain: "顺序应为组 → 容量 → 组内物品，才能保证每组最多选一个。",
    },
  },
  {
    id: 7,
    title: "有依赖背包",
    kicker: "附件只有在主件被选时才能选择。把依赖关系画成树，从叶子向根合并方案。",
    bridge: "上一讲处理互斥，本讲处理前置条件。关键仍是转换：把一棵子树压成一个可选择的方案组。",
    duration: 10,
    sceneTitle: "预算方案：主件与附件必须成套购买",
    objectives: ["识别主件与附件依赖", "把主件组合转为物品组", "理解森林上的树形 DP"],
    formula: "subtree(u) = item(u) + Σ generalized(child)",
    memory: "依赖关系是一棵树：先算子树能提供什么，再把整棵子树当成一个泛化物品。",
    type: "dependency",
    steps: [
      ["镜头和脚架依赖相机", "相机是主件；镜头与脚架是附件。附件不能单买，选中任何附件都必须同时支付相机的预算。"],
      ["列出所有合法套餐", "合法策略只有：不买、只买相机、相机+镜头、相机+脚架、三件全买。单独镜头属于非法方案。"],
      ["把套餐压成一个物品组", "每个套餐计算总价格与总重要度收益；这些套餐互斥，因此整个相机子树可以当作一个分组背包。"],
      ["更深依赖从叶子开始", "若镜头还有滤镜附件，必须先算“镜头子树”，再合并进相机。顺序自然是叶子 → 父亲 → 根。"],
      ["多棵主件树再做背包", "相机、电脑等多个主件各自压成一个泛化物品，最后在总预算中合并，这就是树形 DP。"],
    ],
    quiz: {
      q: "树形依赖背包为什么通常要自底向上处理？",
      options: ["先知道子树方案才能合并父节点", "根节点价值一定最大", "可以减少输入数量"],
      answer: 0,
      explain: "父节点的可行策略由各个子树的费用—价值结果组成，所以必须先解决孩子。",
    },
  },
  {
    id: 8,
    title: "泛化物品",
    kicker: "把“物品”从一个费用—价值点，提升为一条函数：分配 v 容量，它能返回 h(v) 价值。",
    bridge: "上一讲已经把整棵子树当成一个对象。本讲给这种对象正式命名，并学习它们如何相加。",
    duration: 10,
    sceneTitle: "旅行预算：把餐饮与景点变成收益曲线",
    objectives: ["理解泛化物品 h(v)", "看懂两个泛化物品的合并", "用统一视角观察各种背包模型"],
    formula: "(h ⊕ l)(v) = max₀≤k≤v { h(k) + l(v-k) }",
    memory: "一个子问题就是一件泛化物品；解背包，就是把这些费用—价值函数逐步相加。",
    type: "generalized",
    steps: [
      ["一个模块不再只有一个价格", "餐饮计划不是固定物品：分配 0…6 份预算，每个额度都有一个最佳满意度 h(v)。"],
      ["景点计划也是一条曲线", "景点模块 l(v) 同样回答“给 v 份预算，最多获得多少体验值”。复杂子问题因此被压成数组。"],
      ["把总预算切成两份", "总预算 6 可以分成 0+6、1+5、2+4……枚举餐饮得到 k、景点得到 6-k 的所有切法。"],
      ["选出最佳预算分配", "例如餐饮分 2、景点分 4 时，总体验是 h(2)+l(4)。所有切法取最大，得到新的 f(6)。"],
      ["结果还能继续合并", "f(v) 又是一条预算—收益曲线，可继续与交通、住宿等模块合并。泛化物品就是可组合的子问题。"],
    ],
    quiz: {
      q: "合并两个泛化物品时，为什么要枚举 k？",
      options: ["枚举物品编号", "枚举总费用在两者间的分配", "枚举价值的大小"],
      answer: 1,
      explain: "k 和 v-k 表示把总费用 v 分给两个泛化物品的所有可能方式。",
    },
  },
  {
    id: 9,
    title: "问法的变化",
    kicker: "最大价值只是一个问法。记录路径、把 max 换成 sum、让状态保存前 K 名，模型就会长出新答案。",
    bridge: "前八讲搭好了统一模型。最后一讲不再改变物品，而是改变问题要我们输出什么。",
    duration: 9,
    sceneTitle: "同一组物品，切换最优、路径、计数与 Top-K",
    objectives: ["从 DP 状态回溯具体方案", "把最优值改造成方案计数", "理解最优方案数与第 K 优"],
    formula: "max → 路径；sum → 计数；scalar → top-K queue",
    memory: "先确认“状态覆盖了哪些方案”，再决定每个状态要保存最大值、数量、路径还是前 K 名。",
    type: "variants",
    steps: [
      ["固定一组小数据", "容量 5，四件物品分别为 A(重1,值1)、B(2,4)、C(3,4)、D(4,7)。所有问法都基于同一张状态图。"],
      ["输出最优方案", "最优价值是 8，并且有两条路径：A+D 与 B+C。记录每个状态的来源，就能沿箭头回溯具体物品。"],
      ["统计恰好装满的方案", "把 max 改成 sum，容量 5 的两种组合 A+D、B+C 会各贡献 1，因此方案总数是 2。"],
      ["统计最优方案数量", "同时维护 best 与 count：两条路径都达到价值 8，遇到并列最优时将计数相加，答案仍是 2。"],
      ["保留前 K 名", "让每个状态保存有序队列；合并“选”与“不选”的队列，可得到前几名方案，并决定同值方案是否去重。"],
    ],
    quiz: {
      q: "要统计恰好装满容量 v 的方案数，常见改造是？",
      options: ["把 max 改成 sum", "把 max 改成 min", "把容量循环删除"],
      answer: 0,
      explain: "转移覆盖全部策略时，用求和聚合各来源，并以 f[0][0]=1 作为空方案起点。",
    },
  },
];

const dom = {
  nav: document.querySelector("#chapterNav"),
  number: document.querySelector("#chapterNumber"),
  title: document.querySelector("#chapterTitle"),
  kicker: document.querySelector("#chapterKicker"),
  bridge: document.querySelector("#chapterBridge"),
  duration: document.querySelector("#lessonDuration"),
  status: document.querySelector("#lessonStatus"),
  sceneTitle: document.querySelector("#sceneTitle"),
  scene: document.querySelector("#scene"),
  stepCounter: document.querySelector("#stepCounter"),
  stepTitle: document.querySelector("#stepTitle"),
  stepBody: document.querySelector("#stepBody"),
  objectives: document.querySelector("#objectives"),
  formula: document.querySelector("#formula"),
  memory: document.querySelector("#memory"),
  prevStep: document.querySelector("#prevStep"),
  nextStep: document.querySelector("#nextStep"),
  playPause: document.querySelector("#playPause"),
  speed: document.querySelector("#speedSelect"),
  quizQuestion: document.querySelector("#quizQuestion"),
  quizOptions: document.querySelector("#quizOptions"),
  quizFeedback: document.querySelector("#quizFeedback"),
  prevChapter: document.querySelector("#prevChapter"),
  nextChapter: document.querySelector("#nextChapter"),
  prevChapterName: document.querySelector("#prevChapterName"),
  nextChapterName: document.querySelector("#nextChapterName"),
  progressText: document.querySelector("#progressText"),
  progressFill: document.querySelector("#progressFill"),
  theme: document.querySelector("#themeToggle"),
  lesson: document.querySelector("#lesson"),
};

const saved = JSON.parse(localStorage.getItem("pack-nine-progress") || "{}");
const state = {
  chapter: chapterFromHash(),
  step: 0,
  playing: false,
  timer: null,
  completed: new Set(saved.completed || []),
  theme: saved.theme || (matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light"),
};

function chapterFromHash() {
  const match = location.hash.match(/chapter-(\d+)/);
  return Math.min(8, Math.max(0, Number(match?.[1] || 1) - 1));
}

function save() {
  localStorage.setItem(
    "pack-nine-progress",
    JSON.stringify({ completed: [...state.completed], theme: state.theme }),
  );
}

function setTheme(theme) {
  state.theme = theme;
  document.documentElement.dataset.theme = theme;
  dom.theme.setAttribute("aria-label", theme === "dark" ? "切换到浅色主题" : "切换到深色主题");
  save();
}

function renderNav() {
  dom.nav.innerHTML = chapters
    .map(
      (chapter, index) => `
        <button class="nav-item ${index === state.chapter ? "active" : ""} ${
          state.completed.has(chapter.id) ? "done" : ""
        }" data-chapter="${index}" type="button" aria-current="${index === state.chapter ? "page" : "false"}">
          <span class="nav-number">${String(chapter.id).padStart(2, "0")}</span>
          <span class="nav-label">${chapter.title}</span>
          <span class="nav-check" ${
            state.completed.has(chapter.id) ? 'aria-label="已完成"' : 'aria-hidden="true"'
          }>✓</span>
        </button>`,
    )
    .join("");

  dom.nav.querySelectorAll(".nav-item").forEach((button) => {
    button.addEventListener("click", () => goChapter(Number(button.dataset.chapter)));
  });
}

function renderChapter({ focus = true } = {}) {
  stopPlaying();
  const chapter = chapters[state.chapter];
  state.step = 0;
  dom.number.textContent = `第 ${chapter.id} 讲 · ${String(chapter.id).padStart(2, "0")} / 09`;
  dom.title.textContent = chapter.title;
  dom.kicker.textContent = chapter.kicker;
  dom.bridge.textContent = chapter.bridge;
  dom.duration.textContent = `约 ${chapter.duration} 分钟`;
  dom.status.textContent = state.completed.has(chapter.id) ? "已完成 ✓" : "未完成";
  dom.sceneTitle.textContent = chapter.sceneTitle;
  dom.objectives.innerHTML = chapter.objectives.map((item) => `<li>${item}</li>`).join("");
  dom.formula.textContent = chapter.formula;
  dom.memory.textContent = chapter.memory;
  dom.quizQuestion.textContent = chapter.quiz.q;
  dom.quizFeedback.textContent = state.completed.has(chapter.id)
    ? `已完成。${chapter.quiz.explain}`
    : "";
  dom.quizFeedback.className = state.completed.has(chapter.id)
    ? "quiz-feedback success"
    : "quiz-feedback";
  dom.quizOptions.innerHTML = chapter.quiz.options
    .map(
      (option, index) =>
        `<button class="quiz-option ${
          state.completed.has(chapter.id) && index === chapter.quiz.answer ? "correct" : ""
        }" data-answer="${index}" type="button">${option}</button>`,
    )
    .join("");
  dom.quizOptions.querySelectorAll("button").forEach((button) => {
    button.addEventListener("click", answerQuiz);
  });

  const previous = chapters[state.chapter - 1];
  const next = chapters[state.chapter + 1];
  dom.prevChapter.disabled = !previous;
  dom.nextChapter.disabled = !next;
  dom.prevChapterName.textContent = previous?.title || "";
  dom.nextChapterName.textContent = next?.title || "";
  renderNav();
  renderStep();
  renderProgress();
  document.title = `第 ${chapter.id} 讲：${chapter.title} · 背包九讲`;
  if (focus) {
    window.scrollTo({ top: 0, behavior: "smooth" });
    dom.lesson.focus({ preventScroll: true });
  }
}

function renderStep() {
  const chapter = chapters[state.chapter];
  const [title, body] = chapter.steps[state.step];
  dom.stepCounter.textContent = `${String(state.step + 1).padStart(2, "0")} / ${String(
    chapter.steps.length,
  ).padStart(2, "0")}`;
  dom.stepTitle.textContent = title;
  dom.stepBody.textContent = body;
  dom.prevStep.disabled = state.step === 0;
  dom.nextStep.disabled = state.step === chapter.steps.length - 1;
  dom.scene.innerHTML = sceneFor(chapter.type, state.step);
}

function sceneFor(type, step) {
  const scenes = {
    "zero-one": sceneZeroOne,
    complete: sceneComplete,
    multiple: sceneMultiple,
    mixed: sceneMixed,
    "two-d": sceneTwoD,
    group: sceneGroup,
    dependency: sceneDependency,
    generalized: sceneGeneralized,
    variants: sceneVariants,
  };
  return `<div class="scene-inner">${scenes[type](step)}</div>`;
}

function cells(values, active = -1, source = -1) {
  return `<div class="dp-row">${values
    .map(
      (value, index) =>
        `<span class="dp-cell ${index === active ? "active" : ""} ${
          index === source ? "source" : ""
        }" data-index="${index}">${value}</span>`,
    )
    .join("")}</div>`;
}

function sceneZeroOne(step) {
  const rows = [
    { name: "未选物品", meta: "初始行", values: [0, 0, 0, 0, 0, 0, 0] },
    { name: "物品 A", meta: "重 2 · 价值 3", values: [0, 0, 3, 3, 3, 3, 3] },
    { name: "物品 B", meta: "重 3 · 价值 4", values: [0, 0, 3, 4, 4, 7, 7] },
    { name: "物品 C", meta: "重 4 · 价值 5", values: [0, 0, 3, 4, 5, 7, 8] },
  ];
  const visibleRow = Math.min(3, step);
  const focus = step === 1 ? [1, 2] : step === 2 ? [2, 5] : step >= 3 ? [3, 6] : [-1, -1];
  const sources = step === 1
    ? [[0, 0, "take"]]
    : step === 2
      ? [[1, 5, "skip"], [1, 2, "take"]]
      : step === 3
        ? [[2, 6, "skip"], [2, 2, "take"]]
        : [];
  const trace = step === 4 ? new Set(["3-6", "2-2", "1-2", "0-0"]) : new Set();
  const equations = [
    "f[i][v]：只看前 i 件物品，容量为 v 时的最大价值",
    "f[1][2] = max(0, f[0][0] + 3) = 3",
    "f[2][5] = max(f[1][5], f[1][2] + 4) = max(3, 7)",
    "f[3][6] = max(f[2][6], f[2][2] + 5) = max(7, 8)",
    "回溯：f[3][6] → f[2][2] → f[1][2] → f[0][0]",
  ];

  return `
    <div class="knapsack-example" aria-label="示例物品与背包容量">
      <span class="capacity-chip"><b>背包容量</b><strong>6</strong></span>
      <span><b>A</b><small>重量 2</small><em>价值 3</em></span>
      <span><b>B</b><small>重量 3</small><em>价值 4</em></span>
      <span><b>C</b><small>重量 4</small><em>价值 5</em></span>
    </div>
    <div class="dp-table" role="table" aria-label="01 背包二维动态规划表">
      <div class="dp-corner" role="columnheader">物品 \ 容量</div>
      ${[0, 1, 2, 3, 4, 5, 6].map((v) => `<div class="dp-head" role="columnheader">${v}</div>`).join("")}
      ${rows.map((row, ri) => `
        <div class="dp-row-head ${ri > visibleRow ? "future" : ""}" role="rowheader">
          <strong>${row.name}</strong><small>${row.meta}</small>
        </div>
        ${row.values.map((value, vi) => {
          const source = sources.find(([sr, sv]) => sr === ri && sv === vi);
          const classes = [
            "dp-table-cell",
            ri > visibleRow ? "future" : "",
            focus[0] === ri && focus[1] === vi ? "active" : "",
            source ? `source-${source[2]}` : "",
            trace.has(`${ri}-${vi}`) ? "trace" : "",
          ].filter(Boolean).join(" ");
          return `<div class="${classes}" role="cell">${ri > visibleRow ? "·" : value}</div>`;
        }).join("")}
      `).join("")}
    </div>
    <div class="dp-equation-strip"><span>${step + 1}</span><code>${equations[step]}</code></div>`;
}

function sceneComplete(step) {
  const rows = [
    { name: "未选物品", meta: "初始行", values: [0, 0, 0, 0, 0, 0, 0] },
    { name: "物品 A ×∞", meta: "重 2 · 价值 3", values: [0, 0, 3, 3, 6, 6, 9] },
    { name: "物品 B ×∞", meta: "重 3 · 价值 4", values: [0, 0, 3, 4, 6, 7, 9] },
  ];
  const visibleRow = step === 0 ? 0 : step < 3 ? 1 : 2;
  const focus = step === 1 ? [1, 2] : step === 2 ? [1, 4] : step === 3 ? [2, 5] : [-1, -1];
  const sources = step === 1
    ? [[0, 2, "skip"], [1, 0, "take"]]
    : step === 2
      ? [[0, 4, "skip"], [1, 2, "take"]]
      : step === 3
        ? [[1, 5, "skip"], [2, 2, "take"]]
        : [];
  const trace = step === 4 ? new Set(["2-6", "1-6", "1-4", "1-2", "1-0"]) : new Set();
  const equations = [
    "每种物品都可以取 0、1、2…件；表格每一行代表一种物品类型",
    "f[1][2] = max(f[0][2], f[1][0] + 3) = 3",
    "f[1][4] = max(f[0][4], f[1][2] + 3) = 6",
    "f[2][5] = max(f[1][5], f[2][2] + 4) = max(6, 7)",
    "同一行：f[1][0] → f[1][2] → f[1][4] → f[1][6]",
  ];

  return `
    <div class="knapsack-example" aria-label="完全背包示例物品与容量">
      <span class="capacity-chip"><b>背包容量</b><strong>6</strong></span>
      <span><b>A ×∞</b><small>重量 2</small><em>价值 3</em></span>
      <span><b>B ×∞</b><small>重量 3</small><em>价值 4</em></span>
    </div>
    <div class="dp-table" role="table" aria-label="完全背包二维动态规划表">
      <div class="dp-corner" role="columnheader">种类 \\ 容量</div>
      ${[0, 1, 2, 3, 4, 5, 6].map((v) => `<div class="dp-head" role="columnheader">${v}</div>`).join("")}
      ${rows.map((row, ri) => `
        <div class="dp-row-head ${ri > visibleRow ? "future" : ""}" role="rowheader">
          <strong>${row.name}</strong><small>${row.meta}</small>
        </div>
        ${row.values.map((value, vi) => {
          const source = sources.find(([sr, sv]) => sr === ri && sv === vi);
          const classes = [
            "dp-table-cell",
            ri > visibleRow ? "future" : "",
            focus[0] === ri && focus[1] === vi ? "active" : "",
            source ? `source-${source[2]}` : "",
            trace.has(`${ri}-${vi}`) ? "trace" : "",
          ].filter(Boolean).join(" ");
          return `<div class="${classes}" role="cell">${ri > visibleRow ? "·" : value}</div>`;
        }).join("")}
      `).join("")}
    </div>
    <div class="dp-equation-strip"><span>${step + 1}</span><code>${equations[step]}</code></div>`;
}

function sceneMultiple(step) {
  const sizes = step === 0 ? [] : step === 1 ? [1, 2, 4] : [1, 2, 4, 8, 3];
  const selectedFor13 = new Set([1, 4, 8]);
  return `
    <div class="inventory-strip" aria-label="18 件相同宝物">
      ${Array.from({length:18}, (_, i) => `<i class="treasure-dot" style="--order:${i}">◆</i>`).join("")}
    </div>
    <p class="scene-label">${step === 0 ? "库存 18 件 · 每件重量 1 · 每件价值 2" : "二进制捆绑：每个包只能选或不选"}</p>
    ${sizes.length ? `<div class="binary-row">
      ${sizes.map((size, order) => `<div class="binary-block ${step === 3 && selectedFor13.has(size) ? "selected" : ""}" style="--size:${Math.min(size, 7)};--order:${order}"><strong>${size}</strong><small>重 ${size} · 值 ${size * 2}</small></div>`).join("")}
    </div>` : ""}
    <div class="bundle-note">${[
      "逐件拆分会产生大量等价组合",
      "1 + 2 + 4 已能表示取 0…7 件",
      "1 + 2 + 4 + 8 + 3 = 18",
      "取 13 件 = 8 + 4 + 1",
      "18 个相同物品 → 5 个 01 捆绑包",
    ][step]}</div>`;
}

function sceneMixed(step) {
  const cases = [
    ["A · 初见樱", "3 分钟", "美学值 5", "只看 1 次", "01 · 逆序"],
    ["B · 湖畔樱", "2 分钟", "美学值 3", "可看 ∞ 次", "完全 · 顺序"],
    ["C · 夜樱", "2 分钟", "美学值 4", "最多 3 次", "多重 · 拆分"],
  ];
  const active = step >= 1 && step <= 3 ? step - 1 : -1;
  return `
    <div class="case-cards">
      ${cases.map((item, i) => `<div class="case-card ${i === active ? "active" : ""}"><strong>${item[0]}</strong><span>${item[1]} · ${item[2]}</span><small>${item[3]}</small><em>${item[4]}</em></div>`).join("")}
    </div>
    <div class="time-plan">
      <span>0</span><div class="time-track">${step === 4
        ? ["C", "C", "C", "B", "B"].map((x, i) => `<i class="visit-${x.toLowerCase()}" style="--order:${i}">${x}</i>`).join("")
        : `<b style="width:${Math.max(8, (step + 1) * 20)}%"></b>`}</div><span>10 分钟</span>
    </div>
    <p class="scene-label">${step === 4 ? "示例最优计划：夜樱 3 次 + 湖畔樱 2 次 = 美学值 18" : "先读数量限制，再选择对应更新过程"}</p>`;
}

function sceneTwoD(step) {
  const reached = [new Set(["0-0"]), new Set(["0-0","2-1"]), new Set(["0-0","2-1","1-3","3-4"]), new Set(["0-0","2-1","1-3","3-2","3-4","5-3","4-5"]), new Set(["3-4","5-3"])][step];
  const points = Array.from({ length: 36 }, (_, i) => {
    const x = i % 6;
    const y = Math.floor(i / 6);
    const key = `${x}-${y}`;
    return `<span class="grid-dot ${reached.has(key) ? "path" : ""} ${step === 4 && reached.has(key) ? "active" : ""}">${x},${y}</span>`;
  }).join("");
  return `
    <div class="task-strip">
      ${[["A",2,1],["B",1,3],["C",3,2]].map(([name,money,time], i) => `<span class="${step === i + 2 || (step === 4 && i < 3) ? "active" : ""}"><b>任务 ${name}</b><small>${money} 元 · ${time} 分钟</small></span>`).join("")}
    </div>
    <p class="scene-label">横轴：经费 0…5 元 · 纵轴：时间 0…5 分钟</p>
    <div class="grid-2d">${points}</div>
    <p class="scene-label" style="margin-top:18px">${step === 4 ? "高亮终点都完成 2 个任务；任一坐标超过 5 都不合法" : "亮起的格子表示当前可达的（经费，时间）状态"}</p>`;
}

function sceneGroup(step) {
  const groups = [
    ["武器栏", [["木剑",2,3],["长剑",4,6]]],
    ["护甲栏", [["皮甲",2,2],["铁甲",4,5]]],
    ["饰品栏", [["戒指",1,2],["护符",2,3]]],
  ];
  const chosen = step === 1 ? [1,-1,-1] : step === 2 ? [1,0,-1] : step >= 3 ? [1,0,0] : [-1,-1,-1];
  return `
    <div class="equipment-groups">
      ${groups.map(([label, items], gi) => `<div class="equipment-group ${step === gi + 1 ? "active" : ""}"><strong>${label}</strong>${items.map(([name,w,v], ii) => `<span class="equipment-option ${chosen[gi] === ii ? "selected" : ""}"><b>${name}</b><small>重 ${w} · 战力 ${v}</small></span>`).join("")}</div>`).join("")}
    </div>
    <div class="loadout"><span>承重 ${step >= 3 ? "7 / 7" : step === 2 ? "6 / 7" : step === 1 ? "4 / 7" : "0 / 7"}</span><b style="--load:${step >= 3 ? 100 : step === 2 ? 86 : step === 1 ? 57 : 0}%"></b><strong>战力 ${step >= 3 ? 10 : step === 2 ? 8 : step === 1 ? 6 : 0}</strong></div>
    <p class="scene-label">${step === 4 ? "组 → 容量逆序 → 组内候选；每栏最多选中一件" : "当前搭配：长剑 + 皮甲 + 戒指（逐组加入）"}</p>`;
}

function sceneDependency(step) {
  const combos = [["只买相机",4,12],["相机 + 镜头",6,20],["相机 + 脚架",5,14],["三件全买",7,22]];
  return `
    <div class="tree">
      <div class="tree-node ${step >= 2 ? "active" : ""}">📷 相机<br><small>主件 · 价格 4 · 收益 12</small></div>
      <div class="tree-line"></div>
      <div class="tree-level">
        <div class="tree-node ${step >= 1 ? "active" : ""}">镜头<br><small>附件 · 2 / 8</small></div>
        <div class="tree-node ${step >= 1 ? "active" : ""}">脚架<br><small>附件 · 1 / 2</small></div>
      </div>
    </div>
    ${step >= 1 ? `<div class="combo-row">${combos.map(([name,cost,value], i) => `<span class="${step >= 2 && i === Math.min(3, step - 1) ? "active" : ""}"><b>${name}</b><small>价格 ${cost} · 收益 ${value}</small></span>`).join("")}</div>` : '<div class="invalid-combo">镜头单买 ✕　脚架单买 ✕</div>'}
    <p class="scene-label" style="margin-top:16px">${step < 2 ? "附件 → 主件：选择附件必须同时选择相机" : step === 2 ? "四个合法套餐互斥，可压成一个物品组" : "子树自底向上合并，整棵树最终成为一件泛化物品"}</p>`;
}

function bars(values) {
  return `<div class="mini-bars">${values.map((value) => `<i style="--value:${value}"></i>`).join("")}</div>`;
}

function sceneGeneralized(step) {
  const a = [0, 15, 32, 44, 52, 58, 62];
  const b = [0, 12, 25, 45, 60, 70, 78];
  const sum = [0, 15, 32, 45, 60, 77, 92];
  return `
    <div class="curve-stage">
      <div class="curve-card ${step === 0 ? "active" : ""}">${bars(a)}<p class="scene-label">餐饮 h(v)</p></div>
      <span class="curve-op">⊕</span>
      <div class="curve-card ${step === 1 ? "active" : ""}">${bars(b)}<p class="scene-label">景点 l(v)</p></div>
      <span class="curve-op">=</span>
      <div class="curve-card ${step >= 3 ? "active" : ""}" style="${step < 2 ? "opacity:.28" : ""}">${bars(sum)}<p class="scene-label">旅行 f(v)</p></div>
    </div>
    ${step >= 2 ? `<div class="budget-splits">${[[0,6,78],[1,5,85],[2,4,92],[3,3,89]].map(([x,y,val]) => `<span class="${x === 2 && step >= 3 ? "active" : ""}"><b>${x} + ${y}</b><small>体验 ${val}</small></span>`).join("")}</div>` : ""}
    <p class="scene-label" style="margin-top:12px">${step >= 3 ? "总预算 6 的最佳切法：餐饮 2 + 景点 4 = 体验 92" : "每根柱表示分配对应预算时的最大收益"}</p>`;
}

function sceneVariants(step) {
  const items = [
    ["路径", "记录来源并回溯"],
    ["计数", "max 改为 sum"],
    ["最优数", "best + count"],
    ["Top K", "合并有序队列"],
  ];
  const goods = [["A",1,1],["B",2,4],["C",3,4],["D",4,7]];
  return `<div class="variant-goods">${goods.map(([name,w,v]) => `<span><b>${name}</b><small>重 ${w} · 值 ${v}</small></span>`).join("")}</div>
    <div class="variant-list">${items
    .map(
      ([title, note], index) =>
        `<div class="variant-card ${index === Math.min(3, Math.max(0, step - 1)) ? "active" : ""}"><strong>${title}</strong><small>${note}</small></div>`,
    )
    .join("")}</div>
    <div class="variant-solutions">${step === 0 ? "容量 5 · 同一份数据，不同聚合方式" : step === 1 ? "最优路径① A + D = 8　　最优路径② B + C = 8" : step === 2 ? "exactCount[5] = 1 + 1 = 2" : step === 3 ? "best = 8　count(best) = 2" : "Top-3：8（A+D） · 8（B+C） · 7（D）"}</div>`;
}

function moveStep(delta) {
  const last = chapters[state.chapter].steps.length - 1;
  state.step = Math.max(0, Math.min(last, state.step + delta));
  renderStep();
  if (state.playing && state.step === last) stopPlaying();
}

function startPlaying() {
  if (state.step === chapters[state.chapter].steps.length - 1) state.step = 0;
  state.playing = true;
  dom.playPause.textContent = "❚❚ 暂停";
  renderStep();
  scheduleNext();
}

function scheduleNext() {
  clearTimeout(state.timer);
  state.timer = setTimeout(() => {
    if (state.step < chapters[state.chapter].steps.length - 1) {
      state.step += 1;
      renderStep();
      scheduleNext();
    } else {
      stopPlaying();
    }
  }, Number(dom.speed.value));
}

function stopPlaying() {
  clearTimeout(state.timer);
  state.timer = null;
  state.playing = false;
  dom.playPause.textContent = "▶ 自动播放";
}

function goChapter(index) {
  if (index < 0 || index >= chapters.length) return;
  state.chapter = index;
  location.hash = `chapter-${index + 1}`;
  renderChapter();
}

function answerQuiz(event) {
  const chapter = chapters[state.chapter];
  const selected = Number(event.currentTarget.dataset.answer);
  dom.quizOptions.querySelectorAll("button").forEach((button) => {
    button.classList.remove("correct", "wrong");
  });
  if (selected === chapter.quiz.answer) {
    event.currentTarget.classList.add("correct");
    dom.quizFeedback.textContent = `答对了。${chapter.quiz.explain}`;
    dom.quizFeedback.className = "quiz-feedback success";
    state.completed.add(chapter.id);
    dom.status.textContent = "已完成 ✓";
    save();
    renderNav();
    renderProgress();
  } else {
    event.currentTarget.classList.add("wrong");
    dom.quizFeedback.textContent = "还差一点。回到动画对应步骤看一眼，再试一次。";
    dom.quizFeedback.className = "quiz-feedback";
  }
}

function renderProgress() {
  const count = state.completed.size;
  dom.progressText.textContent = `${count} / 9 章完成`;
  dom.progressFill.style.width = `${(count / 9) * 100}%`;
}

dom.prevStep.addEventListener("click", () => {
  stopPlaying();
  moveStep(-1);
});
dom.nextStep.addEventListener("click", () => {
  stopPlaying();
  moveStep(1);
});
dom.playPause.addEventListener("click", () => (state.playing ? stopPlaying() : startPlaying()));
dom.speed.addEventListener("change", () => {
  if (state.playing) scheduleNext();
});
dom.prevChapter.addEventListener("click", () => goChapter(state.chapter - 1));
dom.nextChapter.addEventListener("click", () => goChapter(state.chapter + 1));
dom.theme.addEventListener("click", () => setTheme(state.theme === "light" ? "dark" : "light"));

window.addEventListener("hashchange", () => {
  const next = chapterFromHash();
  if (next !== state.chapter) {
    state.chapter = next;
    renderChapter();
  }
});

document.addEventListener("keydown", (event) => {
  if (["SELECT", "BUTTON"].includes(document.activeElement.tagName)) return;
  if (event.key === " ") {
    event.preventDefault();
    state.playing ? stopPlaying() : startPlaying();
  } else if (event.key === "ArrowLeft") {
    stopPlaying();
    moveStep(-1);
  } else if (event.key === "ArrowRight") {
    stopPlaying();
    moveStep(1);
  }
});

setTheme(state.theme);
renderChapter({ focus: false });
