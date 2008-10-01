package de.lmu.ifi.dbs.elki.algorithm.clustering.biclustering;

import de.lmu.ifi.dbs.elki.algorithm.clustering.Clustering;
import de.lmu.ifi.dbs.elki.algorithm.result.clustering.Cluster;
import de.lmu.ifi.dbs.elki.algorithm.result.clustering.SubspaceClusterModel;
import de.lmu.ifi.dbs.elki.algorithm.result.clustering.biclustering.Bicluster;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.database.AssociationID;
import de.lmu.ifi.dbs.elki.database.Associations;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ObjectAndAssociations;
import de.lmu.ifi.dbs.elki.database.SequentialDatabase;
import de.lmu.ifi.dbs.elki.utilities.Description;
import de.lmu.ifi.dbs.elki.utilities.UnableToComplyException;
import de.lmu.ifi.dbs.elki.utilities.pairs.IntIntPair;

import java.util.BitSet;

/**
 * todo arthur comment
 * @author Arthur Zimek
 */
public class NaivePatternBased extends AbstractBiclustering<DoubleVector> {
    // TODO: will be eventually removed when the subspace clustering is directly implemented
    private Clustering<DoubleVector> subspaceClustering;

    public NaivePatternBased() {
        super();
    }

    @SuppressWarnings("unused")
    private int newColumn(int firstCol, int secondCol) {
        return firstCol * (firstCol + 1) / 2 + firstCol * (getColDim() - firstCol - 1) + (secondCol - firstCol - 1);
    }

    private IntIntPair oldColumns(int newColumn) {
        int firstColumn = 0;
        int secondColumn = 0;
        boolean unequal = true;
        while (unequal) {
            if (newColumn - (getColDim() - firstColumn - 1) >= 0) {
                newColumn -= getColDim() - firstColumn - 1;
                firstColumn++;
            }
            else {
                secondColumn = newColumn + firstColumn + 1;
                unequal = false;
            }
        }
        return new IntIntPair(firstColumn, secondColumn);
    }

    @Override
    protected void biclustering() throws IllegalStateException {
        Database<DoubleVector> inflatedDatabase = new SequentialDatabase<DoubleVector>();
        for (int row = 0; row < getRowDim(); row++) {
            int dim = getColDim();
            dim *= dim - 1;
            dim /= 2;
            double[] values = new double[dim];
            int dimIndex = 0;
            for (int col = 0; col < getColDim() - 1; col++) {
                for (int col2 = col + 1; col2 < getColDim(); col2++) {
                    /*
                    int index = newColumn(col, col2);
                    assert index == dimIndex : "expected: "+dimIndex+", found: "+index;
                    int[] oldColumns = oldColumns(index);
                    assert oldColumns[0] == col && oldColumns[1] == col2 : "\nexpected: first:  "+col+" found: "+oldColumns[0]+"\n"+
                                                                             "          second: "+col2+" found: "+oldColumns[1];
                    */
                    values[dimIndex] = valueAt(row, col) - valueAt(row, col2);
                    dimIndex++;
                }
            }
            // assert dimIndex == dim : "DimIndex=="+dimIndex+", dim=="+dim;
            Associations association = new Associations();
            association.put(AssociationID.ROW_ID, row);
            ObjectAndAssociations<DoubleVector> oa = new ObjectAndAssociations<DoubleVector>(new DoubleVector(values), association);
            try {
                inflatedDatabase.insert(oa);
            }
            catch (UnableToComplyException e) {
                exception(e.getMessage(), e);
            }
        }
        // TODO call uncertain because of the requirement of having a subspace cluster model appended
        // TODO should be replaced by a direct implementation of the subspaceclustering we have in mind
        subspaceClustering.run(inflatedDatabase);

        Cluster<DoubleVector>[] inflatedResult = subspaceClustering.getResult().getClusters();
        for (Cluster<DoubleVector> inflatedCluster : inflatedResult) {
            int[] inflatedRowIDs = inflatedCluster.getClusterIDs();
            BitSet rows = new BitSet();
            for (int id : inflatedRowIDs) {
                rows.set(inflatedDatabase.getAssociation(AssociationID.ROW_ID, id));
            }
            BitSet columns = new BitSet();
            SubspaceClusterModel<DoubleVector> model = (SubspaceClusterModel<DoubleVector>) inflatedCluster.getModel();
            BitSet inflatedAttributes = model.getAttributes();
            for (int i = inflatedAttributes.nextSetBit(0); i >= 0; i = inflatedAttributes.nextSetBit(i + 1)) {
                IntIntPair oldColumns = oldColumns(i);
                columns.set(oldColumns.getFirst());
                columns.set(oldColumns.getSecond());
            }
            Bicluster<DoubleVector> bicluster = defineBicluster(rows, columns);
            addBiclusterToResult(bicluster);
        }
    }

    public Description getDescription() {
        return new Description("Naive Pattern Based", "", "Efficient complete search for biclusters", "");
    }

}
