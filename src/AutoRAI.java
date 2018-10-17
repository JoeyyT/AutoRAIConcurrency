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

    /**
     * Enters queue
     */
    public void tryEnter() {
        lock.lock();
        try {
            if (Thread.currentThread() instanceof Visitor) {
                visitorQueue++;
            } else {
                buyerQueue++;
            }

        } finally {
            printLog();
            lock.unlock();
        }
    }

    /**
     * Exits queue, visits RAI
     * @throws InterruptedException
     */
    public void visit() throws InterruptedException {
        lock.lock();
        try {
            if (Thread.currentThread() instanceof Visitor) {
                while (!visitorCanEnter()) {
                    buyerAvailable.signal();
                    visitorAvailable.await();
                }
                if (tempQueueCounter > 0) {
                    tempQueueCounter--;
                }
                if (tempQueueCounter == 0) {
                    buyerAvailable.signal();
                    currentConsecutiveBuyers = 0;
                }
                visitorQueue--;
                currentVisitors++;
            } else {
                while (!buyerCanEnter()) {
                    buyerAvailable.await();
                }

                currentConsecutiveBuyers++;
                buyerQueue--;
                currentBuyers++;
            }
        } finally {
            printLog();
            lock.unlock();
        }
    }

    /**
     * Leaves RAI
     */
    public void leave() {
        lock.lock();
        try {
            if (Thread.currentThread() instanceof Visitor) {
                currentVisitors--;
                visitorAvailable.signal();
            } else {
                if (currentConsecutiveBuyers == MAX_CONSECUTIVE_BUYERS) {
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
            printLog();
            lock.unlock();
        }
    }

    /**
     * True or false whether visitor can enter
     * @return boolean
     */
    private boolean visitorCanEnter() {
        return !((reachedCapacity() || isBuyerInside() || isBuyerInQueue()));
    }

    /**
     * True or false whether buyer can enter
     * @return boolean
     */
    private boolean buyerCanEnter() {
        return !(consecutiveBuyerReached() || isVisitorInside() || isBuyerInside());
    }

    /**
     * True or false whether RAI reached max capacity
     * @return boolean
     */
    private boolean reachedCapacity() {
        return currentVisitors == RAI_CAPACITY;
    }

    /**
     * True or false whether consecutive buyers is reached
     * @return boolean
     */
    private boolean consecutiveBuyerReached() {
        return currentConsecutiveBuyers == MAX_CONSECUTIVE_BUYERS;
    }

    /**
     *
     * @return boolean
     */
    private boolean isBuyerInside() {
        return currentBuyers > 0;
    }

    /**
     *
     * @return boolean
     */
    private boolean isVisitorInside() {
        return currentVisitors > 0;
    }

    /**
     * True or false whether buyer is in queue or not
     * @return boolean
     */
    private boolean isBuyerInQueue() {
        return buyerQueue > 0 && currentConsecutiveBuyers != MAX_CONSECUTIVE_BUYERS;
    }

    /**
     * Prints statistics
     */
    private void printLog() {
        System.out.println("[AUTORAI]   " + currentVisitors + "/100 Visitors inside.    " + currentBuyers + "/1 Buyer inside.     " + visitorQueue + " Visitors in the queue.     " + buyerQueue + " Buyers in the queue.");
    }
}
