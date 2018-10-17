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

    public void stayAlive() throws InterruptedException{
        Thread.sleep((int) (Math.random() * 1000 + 1000));
    }

    public void buyCar() throws InterruptedException{
        Thread.sleep((int) (Math.random() * 1000 + 1000));
    }
}
