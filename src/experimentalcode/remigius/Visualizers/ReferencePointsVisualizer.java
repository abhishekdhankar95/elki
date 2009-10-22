package experimentalcode.remigius.Visualizers;

import java.util.Iterator;

import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.logging.LoggingUtil;
import de.lmu.ifi.dbs.elki.result.CollectionResult;
import de.lmu.ifi.dbs.elki.visualization.css.CSSClass;
import de.lmu.ifi.dbs.elki.visualization.css.CSSClassManager.CSSNamingConflict;
import de.lmu.ifi.dbs.elki.visualization.svg.SVGPlot;
import de.lmu.ifi.dbs.elki.visualization.svg.SVGUtil;

/**
 * Generates a SVG-Element containing reference points.
 * 
 * TODO: Extend that documentation.
 * 
 * TODO: This class needs testing.
 * 
 * @author Remigius Wojdanowski
 * 
 * @param <NV>
 */
public class ReferencePointsVisualizer<NV extends NumberVector<NV, ?>> extends Projection2DVisualizer<NV> {

  /**
   * Serves reference points.
   */
  private CollectionResult<NV> colResult;

  /**
   * Generic tag to indicate the type of element. Used in IDs, CSS-Classes etc.
   */
  public static final String REFPOINT = "refpoint";

  /**
   * A short name characterizing this Visualizer.
   */
  private static final String NAME = "ReferencePoints";

  /**
   * Initializes this Visualizer.
   * 
   * @param database contains all objects to be processed.
   * @param colResult contains all reference points.
   */
  public void init(VisualizerContext context, CollectionResult<NV> colResult) {
    super.init(NAME, context);
    this.colResult = colResult;
  }

  /**
   * Registers the Reference-Point-CSS-Class at a SVGPlot.
   * 
   * @param svgp the SVGPlot to register the -CSS-Class.
   */
  private void setupCSS(SVGPlot svgp) {
    CSSClass refpoint = new CSSClass(svgp, REFPOINT);
    refpoint.setStatement(SVGConstants.CSS_FILL_PROPERTY, "red");

    try {
      svgp.getCSSClassManager().addClass(refpoint);
      svgp.updateStyleElement();
    }
    catch(CSSNamingConflict e) {
      LoggingUtil.exception("Equally-named CSSClass with different owner already exists", e);
    }
  }

  @Override
  public Element visualize(SVGPlot svgp) {
    Element layer = super.visualize(svgp);
    setupCSS(svgp);
    Iterator<NV> iter = colResult.iterator();

    while(iter.hasNext()) {
      NV v = iter.next();
      Element dot = SVGUtil.svgCircle(svgp.getDocument(), getProjected(v, 0), getProjected(v, 1), 0.005);
      SVGUtil.addCSSClass(dot, REFPOINT);
      layer.appendChild(dot);
    }
    return layer;
  }
}
