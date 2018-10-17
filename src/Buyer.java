public class Buyer extends Thread{

    private AutoRAI autoRAI;

    public Buyer(AutoRAI autoRAI) {
        this.autoRAI = autoRAI;
    }

    public void run() {
        while (true) {
            try {
                stayAlive();
                autoRAI.tryEnter();
                autoRAI.visit();
                buyCar();
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
        Thread.sleep((int) (Math.random() * 1000 + 1000));
    }

    /**
     * Roams the RAI, buys some cars
     * @throws InterruptedException
     */
    public void buyCar() throws InterruptedException{
        Thread.sleep((int) (Math.random() * 1000 + 1000));
    }
}
