package cn.ksmcbrigade.iui_nf.render.math.animation;

import cn.ksmcbrigade.iui_nf.util.MathUtils;
import net.minecraft.util.FastColor;

public class Animator {

    private long start, length;
    private boolean reversed;

    public Animator(long length) {
        this.start = System.currentTimeMillis();
        this.length = length;
        this.reversed = false;
    }

    public double getProgress() {
        long pass = System.currentTimeMillis() - start;
        double rat = pass / (double)length;
        return reversed ? 1 - rat : rat;
    }

    public double getProgressClamped() {
        return MathUtils.clamp(getProgress(), 0.0, 1.0);
    }

    public double getProgressReversed() {
        return 1 - getProgress();
    }

    public double getProgressClampedReversed() {
        return MathUtils.clamp(getProgressReversed(), 0.0, 1.0);
    }

    public boolean isFinished() {
        double p = getProgress();
        return reversed ? p <= 0.0 : p >= 1.0;
    }

    public void reverse() {
        reversed = !reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    public boolean isReversed() {
        return reversed;
    }

    public void reset(long length) {
        this.start = System.currentTimeMillis();
        this.length = length;
    }

    public void reset() {
        this.start = System.currentTimeMillis();
    }

    public static int transformColorOpacity(Animator animator, int hex) {
        return FastColor.ARGB32.color((int) (255 * animator.getProgressClamped()), FastColor.ARGB32.red(hex), FastColor.ARGB32.green(hex), FastColor.ARGB32.blue(hex));
    }
}
