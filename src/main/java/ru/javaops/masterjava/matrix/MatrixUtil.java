package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;

/**
 * gkislin 03.07.2016
 */
public class MatrixUtil {

  // TODO implement parallel multiplication matrixA*matrixB
  public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB,
      ExecutorService executor) throws InterruptedException, ExecutionException {
    final int matrixSize = matrixA.length;
    final int[][] matrixC = new int[matrixSize][matrixSize];

    final int[][] matrixBT = new int[matrixSize][matrixSize];

    for (int i = 0; i < matrixSize; i++) {
      for (int j = 0; j < matrixSize; j++) {
        matrixBT[j][i] = matrixB[i][j];
      }
    }

    class SharedThread implements Runnable {

      private final int start;
      private final int end;

      public SharedThread(int start, int end) {
        this.start = start;
        this.end = end;
      }

      @Override
      public void run() {
        for (int i = start; i < end; i++) {
          for (int j = 0; j < matrixSize; j++) {
            int sum = 0;
            for (int k = 0; k < matrixSize; k++) {
              sum += matrixA[i][k] * matrixBT[j][k];
            }
            matrixC[i][j] = sum;
          }
        }
      }
    }
    final int interval = matrixSize / 10;
    List<Future> futures = new ArrayList<>();
    for (int p = 0; p < matrixSize; p = p + interval) {
      futures.add(executor.submit(new SharedThread(p, p + interval)));
    }

    for (Future future : futures) {
      if (future.get() != null) {
        throw new IllegalArgumentException();
      }
    }

    return matrixC;
  }


  public static int[][] forkThreadMultiply(int[][] matrixA, int[][] matrixB,
      ForkJoinPool forkJoinPool) {
    final int matrixSize = matrixA.length;
    final int[][] matrixC = new int[matrixSize][matrixSize];

    final int[][] matrixBT = new int[matrixSize][matrixSize];

    for (int i = 0; i < matrixSize; i++) {
      for (int j = 0; j < matrixSize; j++) {
        matrixBT[j][i] = matrixB[i][j];
      }
    }

    class MultiplyMatrix extends RecursiveAction {

      private final int seqThreshold = 100;
      private final int start;
      private final int end;

      public MultiplyMatrix(int start, int end) {
        this.start = start;
        this.end = end;
      }

      @Override
      protected void compute() {
        if ((end - start) < seqThreshold) {
          for (int i = start; i < end; i++) {
            for (int j = 0; j < matrixSize; j++) {
              int sum = 0;
              for (int k = 0; k < matrixSize; k++) {
                sum += matrixA[i][k] * matrixBT[j][k];
              }
              matrixC[i][j] = sum;
            }
          }
        } else {
          int middle = (start + end) / 2;
          invokeAll(new MultiplyMatrix(start, middle), new MultiplyMatrix(middle, end));
        }
      }
    }
    forkJoinPool.invoke(new MultiplyMatrix(0, matrixSize));
    return matrixC;
  }

  // TODO optimize by https://habrahabr.ru/post/114797/
  public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
    final int matrixSize = matrixA.length;
    final int[][] matrixC = new int[matrixSize][matrixSize];

    final int[] arrayB = new int[matrixSize];

    for (int i = 0; i < matrixSize; i++) {
      for (int k = 0; k < matrixSize; k++) {
        arrayB[k] = matrixB[k][i];
      }
      for (int j = 0; j < matrixSize; j++) {
        int sum = 0;
        int[] arrayA = matrixA[j];
        for (int k = 0; k < matrixSize; k++) {
          sum += arrayA[k] * arrayB[k];
        }
        matrixC[j][i] = sum;
      }
    }
    return matrixC;
  }

  public static int[][] create(int size) {
    int[][] matrix = new int[size][size];
    Random rn = new Random();

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        matrix[i][j] = rn.nextInt(10);
      }
    }
    return matrix;
  }

  public static boolean compare(int[][] matrixA, int[][] matrixB) {
    final int matrixSize = matrixA.length;
    for (int i = 0; i < matrixSize; i++) {
      for (int j = 0; j < matrixSize; j++) {
        if (matrixA[i][j] != matrixB[i][j]) {
          return false;
        }
      }
    }
    return true;
  }
}
