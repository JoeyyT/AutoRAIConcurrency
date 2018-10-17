import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AutoRAI {

    private final int RAI_CAPACITY = 100;
    private final int MAX_CONSECUTIVE_BUYERS = 4;

    private int visitorQueue;
    private int buyerQueue;
    private int tempQueueCounter;

    private int currentConsecutiveBuyers;
    private int currentVisitors;
    private int currentBuyers;

    private Lock lock;
    private Condition visitorAvailable, buyerAvailable;

    public AutoRAI() {
        lock = new ReentrantLock();
        visitorAvailable = lock.newCondition();
        buyerAvailable = lock.newCondition();
    }

    public void tryEnter() {
        lock.lock();
        try {
            if (Thread.currentThread() instanceof Visitor) {
                visitorQueue++;
            } else {
                buyerQueue++;
            }

        } finally {
            lock.unlock();
        }
    }

    public void visit() throws InterruptedException  {
        lock.lock();
        try {
            if (Thread.currentThread() instanceof Visitor) {
                while(!visitorCanEnter()){
                    buyerAvailable.signal();
                    visitorAvailable.await();
                }
                if(tempQueueCounter > 0){
                    tempQueueCounter--;
                }
                if(tempQueueCounter == 0){
                    buyerAvailable.signal();
                    currentConsecutiveBuyers = 0;
                }
                visitorQueue--;
                currentVisitors++;
            } else {
                while(!buyerCanEnter()){
                    buyerAvailable.await();
                }

                currentConsecutiveBuyers++;
                buyerQueue--;
                currentBuyers++;
            }
        } finally {
            System.out.println("[AUTORAI]   " + currentVisitors + "/100 Visitors inside.    " + currentBuyers + "/1 Buyer inside.     " + visitorQueue + " Visitors in the queue.     " + buyerQueue + " Buyers in the queue.");
            lock.unlock();
        }
    }

    public void leave() {
        lock.lock();
        try {
            if (Thread.currentThread() instanceof Visitor) {
                currentVisitors--;
                visitorAvailable.signal();
            } else {
                if(currentConsecutiveBuyers == MAX_CONSECUTIVE_BUYERS){
                    //store ppl in queue
                    tempQueueCounter = visitorQueue;
                    visitorAvailable.signal();
                } else {
                    visitorAvailable.signal();
                    buyerAvailable.signal();
                }
                currentBuyers--;
            }
        } finally {
            System.out.println("[AUTORAI]   " + currentVisitors + "/100 Visitors inside.    " + currentBuyers + "/1 Buyer inside.     " + visitorQueue + " Visitors in the queue.     " + buyerQueue + " Buyers in the queue.");

            lock.unlock();
        }
    }

    private boolean visitorCanEnter(){
        return !((reachedCapacity() || isBuyerInside() || isBuyerInQueue()));
    }

    private boolean buyerCanEnter(){
        return !(consecutiveBuyerReached() || isVisitorInside() || isBuyerInside());
    }

    private boolean reachedCapacity() {
        return currentVisitors == RAI_CAPACITY;
    }

    private boolean consecutiveBuyerReached(){
        return currentConsecutiveBuyers == MAX_CONSECUTIVE_BUYERS;
    }

    private boolean isBuyerInside(){
        return currentBuyers > 0;
    }

    private boolean isVisitorInside(){
        return currentVisitors > 0;
    }

    private boolean isBuyerInQueue(){
        return buyerQueue > 0 && currentConsecutiveBuyers != MAX_CONSECUTIVE_BUYERS;
    }

}
