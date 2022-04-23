	.text
	.file	"xdp-example.c"
	.section	prog,"ax",@progbits
	.globl	xdp_pass                        # -- Begin function xdp_pass
	.p2align	3
	.type	xdp_pass,@function
xdp_pass:                               # @xdp_pass
# %bb.0:
	r1 = 33
	*(u16 *)(r10 - 8) = r1
	r1 = 7236284523806213712 ll
	*(u64 *)(r10 - 16) = r1
	r1 = 4764857262830019912 ll
	*(u64 *)(r10 - 24) = r1
	r1 = r10
	r1 += -24
	r2 = 18
	call 6
	r0 = 2
	exit
.Lfunc_end0:
	.size	xdp_pass, .Lfunc_end0-xdp_pass
                                        # -- End function
	.type	.L__const.xdp_pass.msg,@object  # @__const.xdp_pass.msg
	.section	.rodata.str1.1,"aMS",@progbits,1
.L__const.xdp_pass.msg:
	.asciz	"Hello, BPF World!"
	.size	.L__const.xdp_pass.msg, 18

	.type	__license,@object               # @__license
	.section	license,"aw",@progbits
	.globl	__license
__license:
	.asciz	"GPL"
	.size	__license, 4

	.addrsig
	.addrsig_sym xdp_pass
	.addrsig_sym __license
