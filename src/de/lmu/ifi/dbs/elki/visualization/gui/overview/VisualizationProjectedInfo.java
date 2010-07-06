package de.lmu.ifi.dbs.elki.visualization.gui.overview;

import de.lmu.ifi.dbs.elki.visualization.VisualizationProjection;
import de.lmu.ifi.dbs.elki.visualization.svg.SVGPlot;
import de.lmu.ifi.dbs.elki.visualization.visualizers.ProjectedVisualizer;
import de.lmu.ifi.dbs.elki.visualization.visualizers.Visualization;
import de.lmu.ifi.dbs.elki.visualization.visualizers.Visualizer;

/**
 * Visualization info that needs projection information.
 * 
 * @author Erich Schubert
 */
class VisualizationProjectedInfo extends VisualizationInfo {
  /**
   * Projection to use in this visualization.
   */
  VisualizationProjection proj;

  /**
   * Visualizer to use.
   */
  ProjectedVisualizer vis;

  /**
   * Constructor.
   * 
   * @param vis Visualizer to use
   * @param proj Projection to use
   * @param width Width
   * @param height Height
   */
  public VisualizationProjectedInfo(ProjectedVisualizer vis, VisualizationProjection proj, double width, double height) {
    super(width, height);
    this.vis = vis;
    this.proj = proj;
  }

  @Override
  public Visualization build(SVGPlot plot, double width, double height) {
    synchronized(vis) {
      return vis.visualize(plot, proj, width, height);
    }
  }

  @Override
  public Visualization buildThumb(SVGPlot plot, double width, double height, int tresolution) {
    synchronized(vis) {
      return vis.makeThumbnail(plot, proj, width, height, tresolution);
    }
  }

  @Override
  public Visualizer getVisualizer() {
    return vis;
  }
}