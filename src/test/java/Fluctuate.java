import services.rumi.StdRanged;

public class Fluctuate {
    public static void main(String[] args) {
        StdRanged a = new StdRanged();
        System.out.println(a.getCurrent());
        a.fluctuate(3.5);
        System.out.println(a.getCurrent());
        a.fluctuate(-1.5);
        System.out.println(a.getCurrent());
        a.fluctuate(-1.5);
        System.out.println(a.getCurrent());
        a.fluctuate(-1.5);
        System.out.println(a.getCurrent());
    }
}
