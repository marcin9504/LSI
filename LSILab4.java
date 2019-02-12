import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class LSILab4 {
    Matrix M;
    Matrix Q;

    public static void main(String[] args) {
        LSILab4 lsi = new LSILab4();
        lsi.go();
    }

    private void go() {
        // init the matrix and the query
        M = readMatrix("data.txt");
        Q = readMatrix("query.txt");

        // print
        System.out.println("Matrix:");
        M.print(3, 2);

        // print the dimensions of the matrix
        System.out.println("M: " + dim(M));
        // print the query
        System.out.println("Query:");
        Q.print(3, 2);
        System.out.println("Q: " + dim(Q));

        // do svd
        svd();
    }

    private void svd() {

        //TODO implement your solution

        SingularValueDecomposition svd = new SingularValueDecomposition(M);

        // get K, S, and D
        Matrix K = svd.getU();
        Matrix S = svd.getS();
        Matrix D = svd.getV();

        // set number of largest singular values to be considered
        int s = 2;

        // cut off appropriate columns and rows from K, S, and D
        Matrix KSubmatrix = K.getMatrix(0, K.getRowDimension() - 1, 0, s - 1);
        Matrix SSubmatrix = S.getMatrix(0, s - 1, 0, s - 1);
        Matrix DSubmatrix = D.getMatrix(0, D.getRowDimension() - 1, 0, s - 1);

        // transform the query vector
        Matrix QTransformed = Q.transpose().times(KSubmatrix).times(SSubmatrix.inverse());

        // compute similaraty of the query and each of the documents, using cosine measure
        for (int i = 0; i < DSubmatrix.getRowDimension(); i++) {
            Matrix row = DSubmatrix.getMatrix(i, i, 0, DSubmatrix.getColumnDimension() - 1);
            Matrix result = row.times(QTransformed.transpose());
            System.out.println("Doc " + Integer.toString(i) + ": " + result.get(0, 0) / (QTransformed.norm2() * row.norm2()));
        }
    }


    // returns the dimensions of a matrix
    private String dim(Matrix M) {
        return M.getRowDimension() + "x" + M.getColumnDimension();
    }

    // reads a matrix from a file
    private Matrix readMatrix(String filename) {
        Vector<Vector<Double>> m = new Vector<Vector<Double>>();
        int colums = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            while (br.ready()) {
                Vector<Double> row = new Vector<Double>();
                m.add(row);
                String line = br.readLine().trim();
                StringTokenizer st = new StringTokenizer(line, ", ");
                colums = 0;
                while (st.hasMoreTokens()) {
                    row.add(Double.parseDouble(st.nextToken()));
                    colums++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int rows = m.size();
        Matrix M = new Matrix(rows, colums);
        int rowI = 0;
        for (Vector<Double> vector : m) {
            int colI = 0;
            for (Double d : vector) {
                M.set(rowI, colI, d);
                colI++;
            }
            rowI++;
        }
        return M;
    }
}
