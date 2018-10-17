public class Visitor extends Thread{

    private AutoRAI autoRAI;

    public Visitor(AutoRAI autoRAI) {
        this.autoRAI = autoRAI;
    }

    public void run() {
        while (true) {
            try {
                stayAlive();
                autoRAI.tryEnter();
                autoRAI.visit();
                roamRAI();
                autoRAI.leave();
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * Roams the world, stays alive
     * @throws InterruptedException
     */
    public void stayAlive() throws InterruptedException{
        Thread.sleep((int) (Math.random() * 5000 + 1000));
    }

    /**
     * Roams RAI
     * @throws InterruptedException
     */
    public void roamRAI() throws InterruptedException{
        Thread.sleep((int) (Math.random() * 1000 + 1000));
    }
}
