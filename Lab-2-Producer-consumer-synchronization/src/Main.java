import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    static List<Double> firstVector = new ArrayList<>();
    static List<Double> secondVector = new ArrayList<>();
    static Integer vectorsSize;
    static private ReentrantLock lockObj = new ReentrantLock();
    static private Condition condition = lockObj.newCondition();
    static private Queue<Double> computedProductPairs = new ArrayDeque<>();
    static private Integer EMPTY_QUEUE_SIZE = 0;
    static private Integer maxQueueSize;
    static private Double scalarProduct = 0.0;

    static public void computeProduct() {
        for (int index = 0; index < vectorsSize; index++) {
            lockObj.lock();
            try {
                while (computedProductPairs.size() == maxQueueSize) {
                    condition.await();
                }
                computedProductPairs.add(firstVector.get(index) * secondVector.get(index));
                condition.signalAll();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            } finally {
                lockObj.unlock();
            }
        }
    }

    static public void addProductToSum() {
        for (int index = 0; index < vectorsSize; index++) {
            lockObj.lock();
            try {
                while (computedProductPairs.size() == EMPTY_QUEUE_SIZE) {
                    condition.await();
                }
                scalarProduct += computedProductPairs.poll();
                condition.signalAll();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            } finally {
                lockObj.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var scanner = new Scanner(System.in);

        System.out.print("Input the size of the vectors: ");
        vectorsSize = scanner.nextInt();

        System.out.print("Input the maximum queue size: ");
        maxQueueSize = scanner.nextInt();

        System.out.print("Input the first vector: ");

        for (int index = 0; index < vectorsSize; index++) {
            firstVector.add(scanner.nextDouble());
        }

        System.out.print("Input the second vector: ");

        for (int index = 0; index < vectorsSize; index++) {
            secondVector.add(scanner.nextDouble());
        }

        Thread producerThread = new Thread(Main::computeProduct);
        Thread consumerThread = new Thread(Main::addProductToSum);

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();

        System.out.printf("The scalar product of the 2 vectors is: %f\n", scalarProduct);
    }
}