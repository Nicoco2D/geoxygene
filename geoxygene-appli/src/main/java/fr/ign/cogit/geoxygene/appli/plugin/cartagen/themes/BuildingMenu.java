/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.genealgorithms.polygon.PolygonAggregation;
import fr.ign.cogit.cartagen.genealgorithms.polygon.PolygonSquaring;
import fr.ign.cogit.cartagen.genealgorithms.polygon.SquarePolygonLS;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventManager;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.generalisation.simplification.SimplificationAlgorithm;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;
import java.lang.Math;

public class BuildingMenu extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Logger logger = Logger.getLogger(BuildingMenu.class.getName());

  private JMenuItem mBatimentSelectionnerTous = new JMenuItem(
      new SelectAction());

  public JCheckBoxMenuItem mBuildingDeplacement = new JCheckBoxMenuItem(
      "Displace buildings");

  public JCheckBoxMenuItem mIdBatiVoir = new JCheckBoxMenuItem("Display id");

  public JCheckBoxMenuItem mVoirTauxSuperposition = new JCheckBoxMenuItem(
      "Display overlapping rate");

  public JCheckBoxMenuItem mVoirAire = new JCheckBoxMenuItem("Voir aire");
  public JCheckBoxMenuItem mVoirAireBut = new JCheckBoxMenuItem("Voir aire but");

  public JCheckBoxMenuItem mVoirAltitude = new JCheckBoxMenuItem(
      "Voir altitude");

  public JCheckBoxMenuItem mVoirOrientationGenerale = new JCheckBoxMenuItem(
      "Voir orientation generale");
  public JCheckBoxMenuItem mVoirOrientationMurs = new JCheckBoxMenuItem(
      "Voir orientation murs");
  public JCheckBoxMenuItem mVoirRosaceOrientationMurs = new JCheckBoxMenuItem(
      "Voir rosace orientation murs");

  public JCheckBoxMenuItem mVoirRosaceEncombrement = new JCheckBoxMenuItem(
      "Voir rosace encombrement");

  public JCheckBoxMenuItem mVoirElongation = new JCheckBoxMenuItem(
      "Voir elongation");

  public JCheckBoxMenuItem mVoirConvexite = new JCheckBoxMenuItem(
      "Voir convexite");

  public JCheckBoxMenuItem mVoirLgPlusPetitCote = new JCheckBoxMenuItem(
      "Voir lg plus petit cote");

  private JMenuItem mParametrisedDilatation = new JMenuItem(
      new ParametrisedDilatationAction());
  private JMenuItem mDilatation = new JMenuItem(new DilatationAction());
  private JMenuItem mPPRE = new JMenuItem(new EnlargeToRectangleAction());
  private JMenuItem mPPREAireConstante = new JMenuItem(new ToRectangleAction());
  private JMenuItem mRotation = new JMenuItem(new RotateAction());
  private JMenuItem mEnveloppeConvexe = new JMenuItem(new ToConvexHullAction());
  private JMenuItem mEnveloppeRectAxe = new JMenuItem(
      new ToRectAxisHullAction());
  private JMenuItem mSimplification = new JMenuItem(new SimplifyAction());
  private JMenuItem mSimpleSquaring = new JMenuItem(new SimpleSquaringAction());
  private JMenuItem mLSSquaring = new JMenuItem(new LSSquaringAction());
  private JMenuItem mAmalgamation = new JMenuItem(new AmalgamationAction());
  
  //BEBOUT
  private JMenuItem mBuildingsOrthogonalization = new JMenuItem(
	      new BuildingsOrthogonalization());
  
  private JMenuItem mBuildingsOrthogonalizationWithOrientation = new JMenuItem(
	      new BuildingsOrthogonalizationWithOrientation());
  
  private JMenuItem mSimplifyBuildings = new JMenuItem(
	      new SimplifyBuildings());
  //-----
  
  private ArrayList<DirectPositionList> historiqueSimplification = new ArrayList<>();
  
  /**
   * Constructor a of the menu from a title.
   * @param title
   */
  public BuildingMenu(String title) {
    super(title);

    this.add(this.mBatimentSelectionnerTous);
    this.addSeparator();

    this.add(this.mBuildingDeplacement);
    this.addSeparator();

    this.add(this.mIdBatiVoir);
    this.addSeparator();

    this.add(this.mVoirTauxSuperposition);
    this.addSeparator();
    this.add(this.mVoirAire);
    this.add(this.mVoirAireBut);
    this.addSeparator();
    this.add(this.mVoirAltitude);
    this.addSeparator();
    this.add(this.mVoirOrientationGenerale);
    this.add(this.mVoirOrientationMurs);
    this.add(this.mVoirRosaceOrientationMurs);
    this.addSeparator();
    this.add(this.mVoirRosaceEncombrement);
    this.addSeparator();
    this.add(this.mVoirElongation);
    this.add(this.mVoirConvexite);
    this.add(this.mVoirLgPlusPetitCote);

    this.addSeparator();

    this.add(this.mParametrisedDilatation);
    this.add(this.mDilatation);
    this.add(this.mPPRE);
    this.add(this.mPPREAireConstante);
    this.add(this.mRotation);
    this.add(this.mEnveloppeConvexe);
    this.add(this.mEnveloppeRectAxe);
    this.add(this.mSimplification);
    JMenu mSquaring = new JMenu("Squaring algorithms");
    mSquaring.add(this.mSimpleSquaring);
    mSquaring.add(this.mLSSquaring);
    this.add(mSquaring);
    this.add(this.mAmalgamation);
    
    //BEBOUT
    this.addSeparator();
    this.add(this.mBuildingsOrthogonalization);
    this.add(this.mBuildingsOrthogonalizationWithOrientation);
    this.add(this.mSimplifyBuildings);
    //-------
  }

  private class SelectAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      for (IBuilding obj : CartAGenDoc.getInstance().getCurrentDataset()
          .getBuildings()) {
        SelectionUtil.addFeatureToSelection(CartAGenPlugin.getInstance()
            .getApplication(), obj);
      }
    }

    public SelectAction() {
      this.putValue(Action.NAME, "Select all buildings");
    }
  }

  private class ParametrisedDilatationAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(appli.getMainFrame().getGui(),
              "Coefficient de dilatation", "Cartagen",
              JOptionPane.PLAIN_MESSAGE);
          double coef = 1.0;
          if (s != null && !s.isEmpty()) {
            coef = Double.parseDouble(s);
          }
          for (IFeature sel : SelectionUtil.getSelectedObjects(appli)) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger.info("Dilatation du batiment " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie initiale: "
                  + ab.getGeom());
            }
            ab.setGeom(CommonAlgorithms.homothetie(ab.getGeom(), coef));
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie finale: "
                  + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public ParametrisedDilatationAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger parametrised dilatation algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger parametrised dilatation");
    }
  }

  private class DilatationAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin
              .getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }
            IBuilding ab = (IBuilding) sel;
            double area = ab.getGeom().area();
            if (area < GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT) {
              ab.eliminate();
            } else {
              double aireMini = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
                  * Legend.getSYMBOLISATI0N_SCALE()
                  * Legend.getSYMBOLISATI0N_SCALE() / 1000000.0;
              if (area < aireMini) {
                GM_Object geom = (GM_Object) CommonAlgorithms.homothetie(
                    ab.getGeom(), Math.sqrt(aireMini / area));
                ab.setGeom(geom);
              }
            }
          }
        }
      });
      th.start();
    }

    public DilatationAction() {
      super();
      this.putValue(Action.NAME, "Self dilatation of selected buildings");
    }

  }

  private class EnlargeToRectangleAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin
              .getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger.info("Transformation en PPRE du batiment "
                + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie initiale: "
                  + ab.getGeom());
            }
            ab.setGeom(SmallestSurroundingRectangleComputation.getSSR(ab
                .getGeom()));
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie finale: "
                  + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public EnlargeToRectangleAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Enlarge to rectangle algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Enlarge to rectangle");
    }
  }

  private class ToRectangleAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin
              .getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger
                .info("Transformation en PPRE avec aire constante du batiment "
                    + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie initiale: "
                  + ab.getGeom());
            }
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Aire initiale: "
                  + ab.getGeom().area());
            }
            ab.setGeom(SmallestSurroundingRectangleComputation
                .getSSRPreservedArea(ab.getGeom()));
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie finale: "
                  + ab.getGeom());
            }
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Aire finale: "
                  + ab.getGeom().area());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public ToRectangleAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Change to rectangle algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Change to rectangle");
    }
  }

  private class RotateAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(CartAGenPlugin.getInstance()
              .getApplication().getMainFrame().getGui(),
              "Angle de rotation (en degres, sens trigo)", "Cartagen",
              JOptionPane.PLAIN_MESSAGE);
          double angle = 0.0;
          if (s != null && !s.isEmpty()) {
            angle = Double.parseDouble(s);
          }
          for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin
              .getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger.info("Rotation du batiment " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie initiale: "
                  + ab.getGeom());
            }
            ab.setGeom(CommonAlgorithms.rotation(ab.getGeom(), angle * Math.PI
                / 180.0));
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie finale: "
                  + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public RotateAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Rotate algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Rotate");
    }
  }

  private class ToConvexHullAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin
              .getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger
                .info("Transformation en enveloppe convexe du batiment " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie initiale: "
                  + ab.getGeom());
            }
            ab.setGeom(ab.getGeom().convexHull());
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie finale: "
                  + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public ToConvexHullAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Change to convex hull algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Change to convex hull");
    }
  }

  private class ToRectAxisHullAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin
              .getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }
            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger
                .info("Transformation en enveloppe rectangulaire parallèle aux axes du batiment "
                    + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie initiale: "
                  + ab.getGeom());
            }
            ab.setGeom(ab.getGeom().envelope().getGeom());
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie finale: "
                  + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public ToRectAxisHullAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Change to rect. axis hull algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Change to rect. axis hull");
    }
  }

  private class SimplifyAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(CartAGenPlugin.getInstance()
              .getApplication().getMainFrame().getGui(), "Seuil", "Cartagen",
              JOptionPane.PLAIN_MESSAGE);
          double coef = 10.0;
          if (s != null && !s.isEmpty()) {
            coef = Double.parseDouble(s);
          }
          for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin
              .getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger.info("Simplification de " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie initiale: "
                  + ab.getGeom().coord().size() + " " + ab.getGeom());
            }
            ab.setGeom(SimplificationAlgorithm.simplification(ab.getGeom(),
                coef));
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie finale: "
                  + ab.getGeom().coord().size() + " " + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public SimplifyAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Simplify algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Simplify");
    }
  }

  private class SimpleSquaringAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(CartAGenPlugin.getInstance()
              .getApplication().getMainFrame().getGui(),
              "Angles to square (radians)", "Cartagen",
              JOptionPane.PLAIN_MESSAGE);
          double angTol = 8 * Math.PI / 180;
          if (s != null && !s.isEmpty()) {
            angTol = Double.parseDouble(s);
          }
          String s2 = JOptionPane.showInputDialog(CartAGenPlugin.getInstance()
              .getApplication().getMainFrame().getGui(), "tolerance (radians)",
              "Cartagen", JOptionPane.PLAIN_MESSAGE);
          double correctTol = 0.6 * Math.PI / 180;
          if (s2 != null && !s2.isEmpty()) {
            correctTol = Double.parseDouble(s2);
          }
          for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin
              .getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger.info("Equarrissage de " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie initiale: "
                  + ab.getGeom().coord().size() + " " + ab.getGeom());
            }
            PolygonSquaring squaring = new PolygonSquaring(ab.getGeom(),
                angTol, correctTol);
            ab.setGeom(squaring.simpleSquaring());
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie finale: "
                  + ab.getGeom().coord().size() + " " + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public SimpleSquaringAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Simple Squaring algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger Simple Squaring");
    }
  }

  private class LSSquaringAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      Thread th = new Thread(new Runnable() {
        @Override
        public void run() {
          String s = JOptionPane.showInputDialog(CartAGenPlugin.getInstance()
              .getApplication().getMainFrame().getGui(),
              "Right angles tol (°)", "Cartagen", JOptionPane.PLAIN_MESSAGE);
          double rightTol = 8.0;
          if (s != null && !s.isEmpty()) {
            rightTol = Double.parseDouble(s);
          }
          String s3 = JOptionPane.showInputDialog(CartAGenPlugin.getInstance()
              .getApplication().getMainFrame().getGui(), "45° angles tol (°)",
              "Cartagen", JOptionPane.PLAIN_MESSAGE);
          double midTol = 8.0;
          if (s3 != null && !s3.isEmpty()) {
            midTol = Double.parseDouble(s3);
          }
          for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin
              .getInstance().getApplication())) {
            if (sel.isDeleted()) {
              continue;
            }
            if (!(sel instanceof IBuilding)) {
              continue;
            }

            IBuilding ab = (IBuilding) sel;
            BuildingMenu.this.logger.info("Equarrissage de " + ab);
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie initiale: "
                  + ab.getGeom().coord().size() + " " + ab.getGeom());
            }
            SquarePolygonLS squaring = new SquarePolygonLS(rightTol, 0.1,
                midTol);
            squaring.setPolygon(ab.getGeom());
            ab.setGeom(squaring.square());
            if (BuildingMenu.this.logger.isLoggable(Level.CONFIG)) {
              BuildingMenu.this.logger.config("Geometrie finale: "
                  + ab.getGeom().coord().size() + " " + ab.getGeom());
            }
            BuildingMenu.this.logger.info(" fin");
          }
        }
      });
      th.start();
    }

    public LSSquaringAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Trigger Least Squares Squaring algorithm on selected buildings");
      this.putValue(Action.NAME, "Trigger LS Squaring");
    }
  }

  private class AmalgamationAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      List<IFeature> selected = SelectionUtil
          .getListOfSelectedObjects(CartAGenPlugin.getInstance()
              .getApplication());
      IFeature feat1 = selected.get(0);
      IFeature feat2 = selected.get(1);
      PolygonAggregation algo = new PolygonAggregation(
          (IPolygon) feat1.getGeom(), (IPolygon) feat2.getGeom());
      IPolygon amalgamated = algo.regnauldAmalgamation(1.0);
      System.out.println(amalgamated);
      feat1.setGeom(amalgamated);
      ((IGeneObj) feat2).eliminate();
    }

    public AmalgamationAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Amalgamation of selected two buildings from Regnauld 98");
      this.putValue(Action.NAME, "Amalgamation of selected two buildings");
    }
  }
  
  /**
   * 
   * @author bebout
   *
   * Méthode permettant l'orthogonalisation des bâtiments sans prendre en compte l'orientation des bâtiment
   * Orthogonalisation dans le repère Monde.
   * 
   * Orthogonalizing buildings
   */
  
private class BuildingsOrthogonalization extends AbstractAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent arg0) {
		for (IBuilding obj : CartAGenDoc.getInstance().getCurrentDataset().getBuildings()) {
			DirectPositionList listePoints =  (DirectPositionList) obj.getGeom().getExterior().getGenerator(0).coord().clone();

			// Découpe des segments du bâtiments en deux segments orthogonaux				
			listePoints = CommonAlgorithms.orthogonalization(listePoints);
			obj.getGeom().getExterior().getGenerator(0).coord().clear();
			obj.getGeom().getExterior().getGenerator(0).coord().addAll(listePoints);
			obj.getGeom().getExterior().getGenerator(0).coord().add(listePoints.get(0)); //Ferme le GM_Ring
		}
	}
	
	public BuildingsOrthogonalization() {
	    this.putValue(Action.NAME, "Buildings Orthogonalization");
	}
}
  
  /**
   * 
   * @author bebout
   *
   * Méthode permettant l'orthogonalisation des bâtiments en fonction de l'orientation de celui-ci.
   * Orthogonalizing buildings in function of their main orientation
   * 
   */
  
	private class BuildingsOrthogonalizationWithOrientation extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			String s = JOptionPane.showInputDialog(CartAGenPlugin.getInstance().getApplication().getMainFrame().getGui(), "Niveau de détails de l'orthogonalisation", "Cartagen", JOptionPane.PLAIN_MESSAGE);
			double nbSegments = 2;
	        if (s != null && !s.isEmpty()) {nbSegments = Double.parseDouble(s);}
	        Population<IBuilding> listeBatiments = new Population<>();
	        
	        for(IFeature feat : SelectionUtil.getListOfSelectedObjects(CartAGenPlugin.getInstance().getApplication())){
	        	try {
					listeBatiments.add((IBuilding) feat);
				} catch (Exception e1) {}
	        }
	        if(listeBatiments.isEmpty()){
				listeBatiments = (Population<IBuilding>) CartAGenDoc.getInstance().getCurrentDataset().getBuildings();
			}
			 
			for (IBuilding obj : listeBatiments) {
				DirectPositionList listePoints = (DirectPositionList) obj.getGeom().getExterior().getGenerator(0).coord().clone();
				DirectPositionList listePointsIntermediaire = new DirectPositionList();
				double angle = 0;

				System.out.println("Aire initiale : " + obj.getGeom().area());

				//On calcule l'orientation principale du bâtiment
				angle = CommonAlgorithms.getMainOrientationPositions(listePoints);
				
				//On effectue une rotation du bâtiment afin que son orientation soit orthogonale au repère général
				
				obj.setGeom(CommonAlgorithms.rotation(obj.getGeom(), Math.toRadians(-angle)));
				listePoints = (DirectPositionList) obj.getGeom().getExterior().getGenerator(0).coord().clone();
				listePointsIntermediaire.clear();
				
				//Analyse des longueurs des côtés
				//En fonction du choix de l'utilisateur pour le niveau de précision de l'orthogonalisation, on place des points intermédiare sur chaque façade du bâtiment
				for (int i = 0; i < listePoints.size() - 1; i++) {
					DirectPosition nextPos = (DirectPosition) listePoints.get(i + 1).clone();
					DirectPosition posInter = (DirectPosition) listePoints.get(0).clone();
					DirectPosition currentPos = (DirectPosition) listePoints.get(i).clone();
					double longueur = Math.sqrt((nextPos.getX() - currentPos.getX())*(nextPos.getX() - currentPos.getX()) + (nextPos.getY() - currentPos.getY())*(nextPos.getY() - currentPos.getY()));
					double distanceX = nextPos.getX() - currentPos.getX();
					double distanceY = nextPos.getY() - currentPos.getY();
					listePointsIntermediaire.add((DirectPosition) currentPos.clone());
					double angleArrete = Math.toDegrees(Math.acos(distanceX/longueur))%180;
					System.out.println(angleArrete);
					if (angleArrete%90 > 5 && angleArrete%90 < 85 && longueur > 10){
						//int nbSegments = 10; //(int) Math.round((longueur / (obj.getGeom().perimeter()/10)));
						for (int j=0; j < nbSegments-1; j++){
							posInter.setY(currentPos.getY() + (distanceY/nbSegments)*(j+1));
							posInter.setX(currentPos.getX() + (distanceX/nbSegments)*(j+1));
							listePointsIntermediaire.add((DirectPosition) posInter.clone());
						}
					}
				}
			
				listePointsIntermediaire.add((DirectPosition) listePointsIntermediaire.get(0).clone());
				listePoints = listePointsIntermediaire.clone();
				listePointsIntermediaire.clear();
				
				// Découpe des segments du bâtiments en deux segments orthogonaux				
				listePointsIntermediaire = CommonAlgorithms.orthogonalization(listePoints);
				
				//On enlève les points inutiles
				listePointsIntermediaire = CommonAlgorithms.removeUselessPoints(listePointsIntermediaire);
				
				obj.getGeom().getExterior().getGenerator(0).coord().clear();
				obj.getGeom().getExterior().getGenerator(0).coord().addAll(listePointsIntermediaire);
 				obj.getGeom().getExterior().getGenerator(0).coord().add(listePointsIntermediaire.get(0));
				obj.setGeom(CommonAlgorithms.rotation(obj.getGeom(), Math.toRadians(angle)));
				listePointsIntermediaire.clear();
				listePoints.clear();
				
				System.out.println("Aire finale : " + obj.getGeom().area());
			}
		}

		public BuildingsOrthogonalizationWithOrientation() {
			this.putValue(Action.NAME, "Buildings Orthogonalization by PPRE");
		}
	}
  
  
  /**
   * 
   * @author bebout
   *
   * Plug-in permettant la simplification de la géométrie des bâtiments
   * Simplify the buildings' geometry
   *
   */
  
	private class SimplifyBuildings extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			new Simplify();
		}

		public SimplifyBuildings() {
			this.putValue(Action.NAME, "Simplify buildings");
		}
	}
	
    class Simplify extends JDialog implements ActionListener {

        /** Serial version UID. */
        private static final long serialVersionUID = 1L;
        private JButton simplifyMoreBtn, simplifyLessBtn;
	
        public Simplify() {
			historiqueSimplification.clear();
            setModal(true);
            setTitle("Simplify building");
            initPanel();

            pack();
            setLocation(1200, 300);
            setSize(180, 200);
            setVisible(true);
        }
        
        private void initPanel(){
        	FormLayout layout = new FormLayout(
                    "10dlu, pref, pref, 10dlu, pref, pref, pref, pref, pref, pref, 10dlu", // Colonnes
                    "10dlu, pref, pref, 10dlu, pref, pref, 10dlu"); // Lignes
            setLayout(layout);
        	CellConstraints cc = new CellConstraints();
        	JPanel btnPanel = new JPanel();
        	simplifyLessBtn = new JButton("-");
        	simplifyLessBtn.addActionListener(this);
        	simplifyLessBtn.setActionCommand("less");
            //add(simplifyLessBtn, cc.xy(2, 3));
            simplifyMoreBtn = new JButton("+");
            simplifyMoreBtn.addActionListener(this);
        	simplifyMoreBtn.setActionCommand("more");
            //add(simplifyMoreBtn, cc.xy(6, 3));
            btnPanel.add(simplifyLessBtn);
            btnPanel.add(simplifyMoreBtn);
            btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
            JLabel lbTitre = new JLabel("Schematization level");
            add(lbTitre, cc.xy(3, 2));
            add(btnPanel, cc.xy(3, 6));
        }
        
        @Override
		public void actionPerformed(ActionEvent evt) {
			Object source = evt.getSource();
            if (source == simplifyLessBtn) {
            	//On récupère les bâtiments selectionnés, on les remplace par leur forme précédemment enregistrées
            	//Ne marche que pour un seul bâtiment
                for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin.getInstance().getApplication())) {
                	if (CartAGenDoc.getInstance().getCurrentDataset().getBuildings().contains(sel)) {
    					IBuilding obj = (IBuilding) sel;
		            	if(historiqueSimplification.size()>0){
		            		simplifyMoreBtn.setEnabled(true);
		            		obj.getGeom().getExterior().getGenerator(0).coord().clear();
		            		DirectPositionList nouvellesPositions = historiqueSimplification.get(historiqueSimplification.size()-1);
		            		obj.getGeom().getExterior().getGenerator(0).coord().addAll(nouvellesPositions);
			 				ProjectFrame selectedProjectFrame = GeOxygeneEventManager.getInstance().getApplication().getMainFrame().getSelectedProjectFrame();
	     					selectedProjectFrame.getLayerViewPanel().repaint();
	     					historiqueSimplification.remove(historiqueSimplification.size()-1);
	     					if(historiqueSimplification.size()==0){
	     						simplifyLessBtn.setEnabled(false);
	     					}
		            	}
                	}
            	}
             }
             if (source == simplifyMoreBtn) {
            	 for (IFeature sel : SelectionUtil.getSelectedObjects(CartAGenPlugin.getInstance().getApplication())) {
     				DirectPositionList listePointsIntermediaire = new DirectPositionList();
     				double angle = 0;
     				if (CartAGenDoc.getInstance().getCurrentDataset().getBuildings().contains(sel)) {
     					IBuilding obj = (IBuilding) sel;
     					listePointsIntermediaire = (DirectPositionList) obj.getGeom().getExterior().getGenerator(0).coord()
     							.clone();
     					if(listePointsIntermediaire.size() == 5){ //Si le polygone est déjà un rectangle
     						break;
     					}
     					historiqueSimplification.add(listePointsIntermediaire.clone());
     					simplifyLessBtn.setEnabled(true);
     					obj.getGeom().getInterior().clear();
     					
     					System.out.println("Aire initiale : " + obj.getGeom().area());
     					
     					angle = CommonAlgorithms.getMainOrientationPositions(listePointsIntermediaire);
     					obj.setGeom(CommonAlgorithms.rotation(obj.getGeom(), Math.toRadians(-angle)));
     	
     					//DirectPositionList listePoints = (DirectPositionList) obj.getGeom().getExterior().getGenerator(0).coord().clone();
     					
     					listePointsIntermediaire = (DirectPositionList) obj.getGeom().getExterior().getGenerator(0).coord()
     							.clone();
     					
     					listePointsIntermediaire.remove(listePointsIntermediaire.size() - 1);
     					boolean inside = true;
     					
     					// Simplification
     					// On vérifie que les segments peuvent se simplifier, qu'il n'y a pas de chevauchement, etc., beaucoup de tests, à revoir
     					for (int i = 0; i < listePointsIntermediaire.size(); i++) {
     						DirectPosition currentPos = (DirectPosition) listePointsIntermediaire.get(i).clone();
     						DirectPosition posTest = (DirectPosition) currentPos.clone();
     						DirectPosition nextPos = (DirectPosition) listePointsIntermediaire.get((i + 1) % (listePointsIntermediaire.size())).clone();
     						DirectPosition nextPos2 = (DirectPosition) listePointsIntermediaire.get((i + 2) % (listePointsIntermediaire.size())).clone();
     						DirectPosition nextPos3 = (DirectPosition) listePointsIntermediaire.get((i + 3) % (listePointsIntermediaire.size())).clone();
     						boolean isOkSimplify = true;
     						DirectPosition newPos = (DirectPosition) currentPos.clone();
     						DirectPosition newPos2 = (DirectPosition) nextPos3.clone();
     						
     						if (Math.abs(currentPos.getX() - nextPos.getX()) < 0.1 && Math.abs(nextPos2.getX() - nextPos3.getX()) < 0.1) {
     							if (((nextPos3.getY() - nextPos2.getY()) * (nextPos.getY() - currentPos.getY()) > 0)&& Math.abs(nextPos.getX() - nextPos2.getX()) < 10000) {
 									double longueurPonderee1 = Math.abs(nextPos.getY() - currentPos.getY());
 									double longueurPonderee2 = Math.abs(nextPos3.getY() - nextPos2.getY());
 									newPos.setX((currentPos.getX()*longueurPonderee1 + nextPos3.getX()*longueurPonderee2) / (longueurPonderee1+longueurPonderee2));
 									newPos2.setX((currentPos.getX()*longueurPonderee1 + nextPos3.getX()*longueurPonderee2) / (longueurPonderee1+longueurPonderee2));
 									posTest.setX(currentPos.getX()-1);
 									for(int l = 0; l < listePointsIntermediaire.size(); l++){
										for (int a = 0, b = listePointsIntermediaire.size()-1; a < listePointsIntermediaire.size(); a++) {
											if( (listePointsIntermediaire.get(a).getY() > posTest.getY()) !=  (listePointsIntermediaire.get(b).getY() > posTest.getY()) &&
											(posTest.getX() < (listePointsIntermediaire.get(b).getX() - listePointsIntermediaire.get(a).getX()) * (posTest.getY() - listePointsIntermediaire.get(a).getY()) / (listePointsIntermediaire.get(b).getY() - listePointsIntermediaire.get(a).getY()) + listePointsIntermediaire.get(a).getX()))
												inside = !inside;
											b=a;
										}
 										if(nextPos3.getX() < currentPos.getX()){
 											if(nextPos3.getY() < currentPos.getY()){
 												if(inside){
 													if(listePointsIntermediaire.get(l).getX() < newPos.getX() && listePointsIntermediaire.get(l).getX() > nextPos3.getX() && listePointsIntermediaire.get(l).getY() > nextPos3.getY() && listePointsIntermediaire.get(l).getY() < currentPos.getY()){
     													isOkSimplify = false;
     												}
 												}
 												else{
 													if(listePointsIntermediaire.get(l).getX() > newPos.getX() && listePointsIntermediaire.get(l).getX() < currentPos.getX() && listePointsIntermediaire.get(l).getY() > nextPos3.getY() && listePointsIntermediaire.get(l).getY() < currentPos.getY()){
     													isOkSimplify = false;
     												}
 												}
 											}
 											else{
 												if(inside){
 													if(listePointsIntermediaire.get(l).getX() > newPos.getX() && listePointsIntermediaire.get(l).getX() < nextPos.getX() && listePointsIntermediaire.get(l).getY() > currentPos.getY() && listePointsIntermediaire.get(l).getY() < nextPos3.getY()){
     													isOkSimplify = false;
     												}
 												}
 												else{
 													if(listePointsIntermediaire.get(l).getX() < newPos.getX() && listePointsIntermediaire.get(l).getX() > nextPos2.getX() && listePointsIntermediaire.get(l).getY() > currentPos.getY() && listePointsIntermediaire.get(l).getY() < nextPos3.getY()){
     													isOkSimplify = false;
     												}
 												}
 											}
 										}
 										else{
 											if(nextPos3.getY() < currentPos.getY()){
 												if (inside){
 													if(listePointsIntermediaire.get(l).getX() < newPos.getX() && listePointsIntermediaire.get(l).getX() > nextPos.getX() && listePointsIntermediaire.get(l).getY() < currentPos.getY() && listePointsIntermediaire.get(l).getY() > nextPos3.getY()){
     													isOkSimplify = false;
     												}
 												}
 												else{
 													if(listePointsIntermediaire.get(l).getX() > newPos.getX() && listePointsIntermediaire.get(l).getX() < nextPos2.getX() && listePointsIntermediaire.get(l).getY() < currentPos.getY() && listePointsIntermediaire.get(l).getY() > nextPos3.getY()){
     													isOkSimplify = false;
     												}
 												}
 											}
 											else{
 												if (inside){
 													if(listePointsIntermediaire.get(l).getX() < newPos.getX() && listePointsIntermediaire.get(l).getX() > nextPos.getX() && listePointsIntermediaire.get(l).getY() > currentPos.getY() && listePointsIntermediaire.get(l).getY() < nextPos3.getY()){
     													isOkSimplify = false;
     												}
 												}
												else{
													if(listePointsIntermediaire.get(l).getX() > newPos.getX() && listePointsIntermediaire.get(l).getX() < nextPos2.getX() && listePointsIntermediaire.get(l).getY() > currentPos.getY() && listePointsIntermediaire.get(l).getY() < nextPos3.getY()){
     													isOkSimplify = false;
     												}
												}
 											}
 										}
 									}
 									if(isOkSimplify){
 										int nbDeleted = 0;
 										while (nbDeleted < 4) {
 											if (listePointsIntermediaire.size() < i) {
 												listePointsIntermediaire.remove(0);
 											} else {
 												listePointsIntermediaire.remove(i % (listePointsIntermediaire.size()));
 											}
 											nbDeleted++;
 										}
 										if (i > listePointsIntermediaire.size() + 1) {
 											listePointsIntermediaire.add(0, newPos);
 										} else {
 											listePointsIntermediaire.add(i % (listePointsIntermediaire.size() + 1), newPos);
 										}
 										if (i > listePointsIntermediaire.size()) {
 											listePointsIntermediaire.add(1, newPos2);
 										} else {
 											listePointsIntermediaire.add((i + 1) % (listePointsIntermediaire.size()), newPos2);
 										}
 									}
     							}
     						}
     						else if (Math.abs(currentPos.getY() - nextPos.getY()) < 0.1 && Math.abs(nextPos2.getY() - nextPos3.getY()) < 0.1) {
     							if (((nextPos3.getX() - nextPos2.getX()) * (nextPos.getX() - currentPos.getX()) > 0) && Math.abs(nextPos.getY() - nextPos2.getY()) < 10000) {
 									double longueurPonderee1 = Math.abs(nextPos.getX() - currentPos.getX());
 									double longueurPonderee2 = Math.abs(nextPos3.getX() - nextPos2.getX());
 									newPos.setY((currentPos.getY()*longueurPonderee1 + nextPos3.getY()*longueurPonderee2) / (longueurPonderee1+longueurPonderee2));
 									newPos2.setY((currentPos.getY()*longueurPonderee1 + nextPos3.getY()*longueurPonderee2) / (longueurPonderee1+longueurPonderee2));
 									posTest.setY(currentPos.getX()-1);
 									for(int l = 0; l < listePointsIntermediaire.size(); l++){
 										for (int a = 0, b = listePointsIntermediaire.size()-1; a < listePointsIntermediaire.size(); a++) {
											if( (listePointsIntermediaire.get(a).getY() > posTest.getY()) !=  (listePointsIntermediaire.get(b).getY() > posTest.getY()) &&
											(posTest.getX() < (listePointsIntermediaire.get(b).getX() - listePointsIntermediaire.get(a).getX()) * (posTest.getY() - listePointsIntermediaire.get(a).getY()) / (listePointsIntermediaire.get(b).getY() - listePointsIntermediaire.get(a).getY()) + listePointsIntermediaire.get(a).getX()))
												inside = !inside;
											b=a;
										}
 										if(nextPos3.getX() < currentPos.getX()){
 											if(nextPos3.getY() < currentPos.getY()){
 												if(inside){
 													if(listePointsIntermediaire.get(l).getX() > nextPos3.getX() && listePointsIntermediaire.get(l).getX() < currentPos.getX() && listePointsIntermediaire.get(l).getY() > nextPos2.getY() && listePointsIntermediaire.get(l).getY() < newPos.getY()){
 														isOkSimplify = false;
 													}
 												}
 												else{
 													if(listePointsIntermediaire.get(l).getX() < nextPos3.getX() && listePointsIntermediaire.get(l).getX() > currentPos.getX() && listePointsIntermediaire.get(l).getY() < nextPos2.getY() && listePointsIntermediaire.get(l).getY() > newPos.getY()){
 														isOkSimplify = false;
 													}
 												}
 												
 											}
 											else{
 												if(inside){
 													if(listePointsIntermediaire.get(l).getX() > nextPos3.getX() && listePointsIntermediaire.get(l).getX() < currentPos.getX() && listePointsIntermediaire.get(l).getY() > currentPos.getY() && listePointsIntermediaire.get(l).getY() < newPos.getY()){
     													isOkSimplify = false;
     												}
 												}
 												else{
 													if(listePointsIntermediaire.get(l).getX() < currentPos.getX() && listePointsIntermediaire.get(l).getX() > nextPos3.getX() && listePointsIntermediaire.get(l).getY() < nextPos3.getY() && listePointsIntermediaire.get(l).getY() > newPos.getY()){
     													isOkSimplify = false;
     												}
 												}
 											}
 										}
 										else{
 											if(nextPos3.getY() < currentPos.getY()){
 												if(inside){
 													if(listePointsIntermediaire.get(l).getX() > currentPos.getX() && listePointsIntermediaire.get(l).getX() < nextPos3.getX() && listePointsIntermediaire.get(l).getY() < newPos.getY() && listePointsIntermediaire.get(l).getY() > nextPos3.getY()){
     													isOkSimplify = false;
     												}
 												}
 												else{
 													if(listePointsIntermediaire.get(l).getX() > currentPos.getX() && listePointsIntermediaire.get(l).getX() < nextPos3.getX() && listePointsIntermediaire.get(l).getY() > newPos.getY() && listePointsIntermediaire.get(l).getY() < currentPos.getY()){
     													isOkSimplify = false;
     												}
 												}
 											}
 											else{
 												if(inside){
 													if(listePointsIntermediaire.get(l).getX() > currentPos.getX() && listePointsIntermediaire.get(l).getX() < nextPos3.getX() && listePointsIntermediaire.get(l).getY() < newPos.getY() && listePointsIntermediaire.get(l).getY() > currentPos.getY()){
     													isOkSimplify = false;
     												}
 												}
 												else{
 													if(listePointsIntermediaire.get(l).getX() > currentPos.getX() && listePointsIntermediaire.get(l).getX() < nextPos3.getX() && listePointsIntermediaire.get(l).getY() > newPos.getY() && listePointsIntermediaire.get(l).getY() < nextPos3.getY()){
     													isOkSimplify = false;
     												}
 												}
 											}
 										}
 									}
 									if(isOkSimplify){
 										int nbDeleted = 0;
 										while (nbDeleted < 4) {
 											if (listePointsIntermediaire.size() < i) {
 												listePointsIntermediaire.remove(0);
 											} else {
 												listePointsIntermediaire.remove(i % (listePointsIntermediaire.size()));
 											}
 											nbDeleted++;
 										}
 										if (i > listePointsIntermediaire.size() + 1) {
 											listePointsIntermediaire.add(0, newPos);
 										} else {
 											listePointsIntermediaire.add(i % (listePointsIntermediaire.size() + 1), newPos);
 										}
 										if (i > listePointsIntermediaire.size()) {
 											listePointsIntermediaire.add(1, newPos2);
 										} else {
 											listePointsIntermediaire.add((i + 1) % (listePointsIntermediaire.size()), newPos2);
 										}
 									}
     							}
     						}
     					}
     	
     					obj.getGeom().getExterior().getGenerator(0).coord().clear();
     					obj.getGeom().getExterior().getGenerator(0).coord().addAll(listePointsIntermediaire);
     					obj.getGeom().getExterior().getGenerator(0).coord().add(listePointsIntermediaire.get(0));
     			
     					if(obj.getGeom().getExterior().getGenerator(0).coord().size() == 5){ // Si le bâtiment est simplifié au max
     						simplifyMoreBtn.setEnabled(false);
     					}
     					
     					// Rotation du bâtiment dans son orientation initiale
     					
     					obj.setGeom(CommonAlgorithms.rotation(obj.getGeom(), Math.toRadians(angle)));
     					
     					System.out.println("Aire finale : " + obj.getGeom().area());
     					ProjectFrame selectedProjectFrame = GeOxygeneEventManager.getInstance().getApplication().getMainFrame().getSelectedProjectFrame();
     					selectedProjectFrame.getLayerViewPanel().repaint();
     				}
     			}
     		}
		}
    }
}
