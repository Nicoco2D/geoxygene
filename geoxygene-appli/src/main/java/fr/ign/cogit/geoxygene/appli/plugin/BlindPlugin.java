package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;

public class BlindPlugin implements GeOxygeneApplicationPlugin, ActionListener
{
	/** Logger. */
	  private final static Logger LOGGER = Logger.getLogger(BlindPlugin.class.getName());
	  
	  /** GeOxygeneApplication */
	  private GeOxygeneApplication application = null;

	  public BlindPlugin() {
	  }
	  
	  /**
	   * Initialize the plugin.
	   * @param application the application
	   */
	  @Override
	  public final void initialize(final GeOxygeneApplication application) {
	    this.application = application;
	    
	    JMenuBar menuBar = application.getMainFrame().getMenuBar();
	    JMenu blindMenu = new JMenu("Blind");
	    
	    JMenuItem menuItem = new JMenuItem("Noms en braille");
	    menuItem.addActionListener(this);
	    
	    blindMenu.add(menuItem);

	    
	    menuBar.add(blindMenu, menuBar.getMenuCount() - 1);

	  }
	  
	  @SuppressWarnings("unchecked")
	  @Override
	  public void actionPerformed(final ActionEvent e) {

		  /**
	    // On récupère la couche sélectionnée
	    ProjectFrame project = this.application.getMainFrame().getSelectedProjectFrame();
	    Set<Layer> selectedLayers = project.getLayerLegendPanel().getSelectedLayers();
	    if (selectedLayers.size() != 1) {
	      javax.swing.JOptionPane.showMessageDialog(null, "You need to select one (and only one) layer.");
	      GaussianFilterPlugin.LOGGER.error("You need to select one (and only one) layer.");
	      return;
	    }
	    Layer layer = selectedLayers.iterator().next();
	    
	    // On propose le champ de saisie du paramètre sigma pour le filtrage gaussien.
	    double sigma = Double.parseDouble(JOptionPane.showInputDialog(GaussianFilterPlugin.this.application.getMainFrame(), "Sigma"));
	    
	    // On construit une population de DefaultFeature
	    Population<DefaultFeature> pop = new Population<DefaultFeature>("GaussianFilter " + layer.getName() + " " + sigma);
	    pop.setClasse(DefaultFeature.class);
	    pop.setPersistant(false);
	    for (IFeature f : layer.getFeatureCollection()) {
	      ILineString line = null;
	      if (ILineString.class.isAssignableFrom(f.getGeom().getClass())) {
	        line = (ILineString) f.getGeom();
	      } else {
	        if (IMultiCurve.class.isAssignableFrom(f.getGeom().getClass())) {
	          line = ((IMultiCurve<ILineString>) f.getGeom()).get(0);
	        }
	      }
	      // On ajoute à la population l'arc lissé
	      pop.nouvelElement(GaussianFilter.gaussianFilter(line, sigma, 1));
	    }
	    
	    // Créer les métadonnées du jeu correspondant et on l'ajoute à la population
	    FeatureType newFeatureType = new FeatureType();
	    newFeatureType.setGeometryType(ILineString.class);
	    pop.setFeatureType(newFeatureType);
	    
	    // On ajoute au ProjectFrame la nouvelle couche créée à partir de la nouvelle population
	    project.getDataSet().addPopulation(pop);
	    project.addFeatureCollection(pop, pop.getNom(), layer.getCRS());
	    **/
	  }

}
