public class Simulation {
    public static void main(String[] args) {
        AutoRAI autoRAI = new AutoRAI();
        Thread[] visitor = new Thread[100];
        for (int i = 0; i < 100; i++) {
            int buyerChance = (int) (Math.random() * 100);
            if (buyerChance < 5) {
                visitor[i] = new Buyer(autoRAI);
                visitor[i].start();
            } else {
                visitor[i] = new Visitor(autoRAI);
                visitor[i].start();
            }
        }
    }
}
