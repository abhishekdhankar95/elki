/*
 * This file is part of ELKI:
 * Environment for Developing KDD-Applications Supported by Index-Structures
 *
 * Copyright (C) 2018
 * ELKI Development Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.initialization;

import org.junit.Test;

import de.lmu.ifi.dbs.elki.algorithm.clustering.AbstractClusterAlgorithmTest;
import de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.CLARA;
import de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.KMeans;
import de.lmu.ifi.dbs.elki.algorithm.clustering.kmeans.SingleAssignmentKMeans;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.utilities.ELKIBuilder;

/**
 * Test the O(kn) initialization "linear BUILD" from PAM++.
 *
 * @author Erich Schubert
 */
public class LinearBUILDInitialMeansTest extends AbstractClusterAlgorithmTest {
  @Test
  public void testLinearBUILDInitialMeans() {
    Database db = makeSimpleDatabase(UNITTEST + "different-densities-2d-no-noise.ascii", 1000);
    Clustering<?> result = new ELKIBuilder<SingleAssignmentKMeans<DoubleVector>>(SingleAssignmentKMeans.class) //
        .with(KMeans.K_ID, 5) //
        .with(KMeans.SEED_ID, 5) //
        .with(KMeans.INIT_ID, LinearBUILDInitialMeans.class) //
        .build().run(db);
    testFMeasure(db, result, 0.9901);
    testClusterSizes(result, new int[] { 195, 200, 200, 200, 205 });
  }

  @Test
  public void testLinearBUILDInitialMedoids() {
    Database db = makeSimpleDatabase(UNITTEST + "different-densities-2d-no-noise.ascii", 1000);
    Clustering<?> result = new ELKIBuilder<CLARA<DoubleVector>>(CLARA.class) //
        .with(KMeans.K_ID, 5) //
        .with(KMeans.SEED_ID, 5) //
        .with(KMeans.INIT_ID, LinearBUILDInitialMeans.class) //
        .with(KMeans.MAXITER_ID, 1) //
        .with(CLARA.Parameterizer.NOKEEPMED_ID) //
        .with(CLARA.Parameterizer.SAMPLESIZE_ID, 10) //
        .with(CLARA.Parameterizer.RANDOM_ID, 0) //
        .build().run(db);
    testFMeasure(db, result, 0.99602);
    testClusterSizes(result, new int[] { 198, 200, 200, 200, 202 });
  }
}
