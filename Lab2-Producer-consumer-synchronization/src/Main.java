import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class Main {
    static Vector<Double> firstVector = new Vector<>();
    static Vector<Double> secondVector = new Vector<>();

    static Integer vectorsSize;

    static private ReentrantLock lockObj = new ReentrantLock();
    static private Condition producerComputedProductPair = lockObj.newCondition();
    static private Condition consumerAddedProductToSum = lockObj.newCondition();
    static private Double computedProductPair;
    static private Double scalarProduct = 0.0;

    static public void computeProduct() {
        for (int index = 0; index < vectorsSize; index++) {
            lockObj.lock();
            try {
                while (computedProductPair != null) {
                    consumerAddedProductToSum.await();
                }
                computedProductPair = firstVector.get(index) * secondVector.get(index);
                producerComputedProductPair.signalAll();

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
                while (computedProductPair == null) {
                    producerComputedProductPair.await();
                }
                scalarProduct += computedProductPair;
                computedProductPair = null;
                consumerAddedProductToSum.signalAll();

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