package com.davis.utilities.result.compare.utils;

import aliceinnets.python.PythonScriptUtil;
import aliceinnets.python.jyplot.JyPlot;
import aliceinnets.util.OneLiners;
import com.davis.utilities.result.compare.confusion.ConfusionMatrix;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This software was created for rights to this software belong to appropriate licenses and
 * restrictions apply.
 *
 * @author Samuel Davis created on 10/3/17.
 */
public class Utils {
  private Utils() {};

  public static List<Integer> getIndexesOf(String path, char indexChar) {
    List<Integer> list = new ArrayList<>();

    for (int i = 0; i < path.length(); i++) {
      if (path.charAt(i) == indexChar) {
        list.add(i);
      }
    }
    return list;
  }

  public static String getFilename(String filePath) {
    //Remove trailing slash
    char lastChar = filePath.charAt(filePath.length() - 1);
    if (lastChar == '/') {
      filePath = filePath.substring(0, filePath.length() - 2);
    }
    return StringUtils.substringAfterLast(filePath, "/");
  }

  public static String getClassName(String filePath) {
    //Remove trailing slash
    char lastChar = filePath.charAt(filePath.length() - 1);
    if (lastChar == '/') {
      filePath = filePath.substring(0, filePath.length() - 2);
    }

    List<Integer> slashIndexes = getIndexesOf(filePath, '/');
    int lastIndex = -1;
    int secondToLast = -1;
    String className = null;
    if (slashIndexes.size() >= 2) {
      lastIndex = slashIndexes.get(slashIndexes.size() - 1);

      secondToLast = slashIndexes.get(slashIndexes.size() - 2);
      className = filePath.substring(secondToLast+1, lastIndex);
    }

    return className;
  }

  public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
    return map.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
            ));
  }
  public void chartTestCool(ConfusionMatrix confusionMatrix) {
    double[] x = OneLiners.linspace(0, 10, 100);
    double[] y = new double[x.length];
    for (int i = 0; i < y.length; i++) {
      y[i] = Math.sin(x[i]);
    }

    y[y.length / 2] = Double.POSITIVE_INFINITY;
    y[y.length / 3] = Double.NEGATIVE_INFINITY;
    y[y.length * 2 / 3] = Double.NaN;

    double[] z = new double[x.length];
    for (int i = 0; i < y.length; i++) {
      z[i] = Math.cos(x[i]);
    }

    double[][] f = new double[x.length][x.length];
    for (int i = 0; i < f.length; i++) {
      for (int j = 0; j < f[0].length; j++) {
        f[i][j] = Math.sin(x[i]) + Math.sin(x[j]);
      }
    }

    OneLiners.rmdirs(PythonScriptUtil.DEFAULT_PATH);
    //		JyPlot.setPythonPath("/usr/local/bin/python3");

    JyPlot plt = new JyPlot();
    plt.write("from matplotlib import cm");
    plt.figure();
    plt.subplot(2, 2, 1);
    plt.hist(z, 30);
    plt.grid();
    //		plt.figure();
    plt.subplot(2, 2, 2);
    plt.scatter(y, z);
    plt.grid();
    //		plt.figure();
    plt.subplot(2, 2, 3);
    plt.plot(x, y, "o");
    plt.plot(x, z, "label='a'");
    plt.grid();
    plt.xlabel("$f$");
    plt.ylabel("$A$");
    plt.ylim(-2.0, 2.0);
    plt.legend("loc='lower left'");
    //		plt.savefig("r'"+PythonScript.DEFAULT_PATH+System.nanoTime()+".pdf'");
    //		plt.figure();
    plt.subplot(2, 2, 4);
    //		plt.contourf(f, "cmap = cm.jet");
    plt.contourf(x, x, f, 50, "cmap = cm.jet");
    plt.colorbar();
    plt.grid();
    plt.xlabel("$T_e$ [keV]");
    plt.ylabel("$n_e$ [$10^{19} m^{-3}$]");
    plt.savefig("r'" + PythonScriptUtil.DEFAULT_PATH + System.nanoTime() + ".pdf'");
    plt.tight_layout();
    plt.show();
    plt.exec();
  }

  public static <T> boolean contains(final T[] array, final T v) {
    for (final T e : array) {
      if (e == v || v != null && v.equals(e)) {
        return true;
      }
    }
    return false;
  }
}
