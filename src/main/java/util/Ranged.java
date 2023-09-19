package util;

public class Ranged {
    private static final double FLUCTUATION_RATE = 0.5;

    private double current;
    private double min;
    private double max;

    public Ranged(double current, double min, double max) {
        this.current = current;
        this.min = min;
        this.max = max;
    }

    public double getCurrent() {
        return current;
    }

    public void fluctuate(double amount) {
        // use sigmoid function to fluctuate
        double fluctuation = 2 * MathUtil.sigmoid(amount) - 1;
        setCurrent(current + fluctuation * FLUCTUATION_RATE);
    }

    public void fluctuate(Ranged amount) {
        // use sigmoid function to fluctuate
        double fluctuation = 2 * MathUtil.sigmoid(amount.current) - 1;
        setCurrent(current + fluctuation * FLUCTUATION_RATE);
    }

    private void setCurrent(double current) {
        if(current < min) {
            current = min;
        }
        if(current > max) {
            current = max;
        }
        this.current = current;
    }
}
