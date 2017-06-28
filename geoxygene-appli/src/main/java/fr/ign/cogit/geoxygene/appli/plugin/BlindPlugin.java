package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventManager;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.render.RenderUtil;
import fr.ign.cogit.geoxygene.style.LabelPlacement;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LinePlacement;
import fr.ign.cogit.geoxygene.style.Placement;
import fr.ign.cogit.geoxygene.style.PointPlacement;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;
import fr.ign.cogit.geoxygene.util.ColorUtil;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import java_cup.sym;

public class BlindPlugin implements GeOxygeneApplicationPlugin, ActionListener
{
	public final static boolean IS_BLIND_ENABLE = true;
	
	/** Logger. */
	  private final static Logger LOGGER = Logger.getLogger(BlindPlugin.class.getName());
	  
	  /** GeOxygeneApplication */
	  private GeOxygeneApplication application = null;
	  
	  /** Noms des sous menus */
	  private static String itemBrailleName = "Noms braille";
	  private static String itemGreyLevelMap = "Map niveau gris";
	  private static String itemParameters = "Parametres";


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
	    
	    JMenuItem menuItemSetScale = new JMenuItem(new SetScaleAction());
	    menuItemSetScale.addActionListener(this);
	    
	    JMenu menuLayers = new JMenu("Layers");
	    
	    JMenuItem menuItemDeleteUselessLayers = new JMenuItem(new DeleteUselessLayersAction());
	    menuItemDeleteUselessLayers.addActionListener(this);
	    
	    JMenuItem menuItemOrderLayers = new JMenuItem(new OrderLayersAction());
	    menuItemOrderLayers.addActionListener(this);
	    
	    JMenuItem menuSLD = new JMenuItem(new LoadSLDAction());
	    menuSLD.addActionListener(this);
	    
	    JMenuItem menuSave = new JMenuItem(new SaveImageAction());
	    menuSave.addActionListener(this);
	    
	    blindMenu.add(menuItemSetScale);
	    blindMenu.add(menuLayers);
	    menuLayers.add(menuItemDeleteUselessLayers);
	    menuLayers.add(menuItemOrderLayers);
	    blindMenu.add(menuSLD);
	    blindMenu.addSeparator();
	    blindMenu.add(menuSave);

	    menuBar.add(blindMenu, menuBar.getMenuCount() - 1);

	  }
	  
	  @SuppressWarnings("unchecked")
	  @Override
	  public void actionPerformed(final ActionEvent e) {
 
	  }

	/**
	   * Set the scale of the map with the scale 1:1200
	   * 
	   * @author Nicolas
	   * 
	   */
	class SetScaleAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			application.getMainFrame().getSelectedProjectFrame().getLayerViewPanel().getViewport()
				.setScale(12000*LayerViewPanel.getMETERS_PER_PIXEL());
		}
	
		public SetScaleAction() {
		      this.putValue(Action.SHORT_DESCRIPTION,
		          "Set the scale of the map to 1:1200");
		      this.putValue(Action.NAME, "Set scale");
		    }
	}
	
	/**
	   * Load the SLD for the level grey map
	   * 
	   * @author Nicolas
	   * 
	   */
	class LoadSLDAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
				
		@Override
		public void actionPerformed(ActionEvent arg0) {  
			for(String name : loadSldList()){
		        File file = new File("/sld-blind/"+name);
		        if (file != null)
		            try {
		            	application.getMainFrame().getSelectedProjectFrame().loadSLD(file);
		            } catch (Exception e1) {
		                e1.printStackTrace();
		            }
			}
			application.getMainFrame().getSelectedProjectFrame().getLayerViewPanel().setBackground(new Color(0x808080));
		}
	
		private ArrayList<String> loadSldList() {
			ArrayList<String> sldToLoad = new ArrayList<String>();
			sldToLoad.add("buildings_blind_sld.xml");
			sldToLoad.add("amenity_blind_sld.xml");
			sldToLoad.add("leisure_blind_sld.xml");
			sldToLoad.add("railway_blind_sld.xml");
			sldToLoad.add("roads_blind_sld.xml");
			sldToLoad.add("waterway_blind_sld.xml");
			return sldToLoad;
		}

		public LoadSLDAction() {
		      this.putValue(Action.SHORT_DESCRIPTION,
		          "Load all SLD used for the level grey map");
		      this.putValue(Action.NAME, "Load SLD");
		    }
	}
	
	/**
	   * Delete the useless layers
	   * 
	   * @author Nicolas
	   * 
	   */
	class DeleteUselessLayersAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		
		private ArrayList<String> toDelete;
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			fillToDeleteList();
			application.getMainFrame().getSelectedProjectFrame().getLayerLegendPanel().removeLayersFromBlindPlugin(toDelete);
		}
	
		private void fillToDeleteList() {
			toDelete = new ArrayList<String>();
			toDelete.add("hospitals");
			toDelete.add("schools");
			toDelete.add("landUseAreas");
			toDelete.add("pointsOfInterest");
			toDelete.add("trees");
			toDelete.add("cycleWay");
			
		}

		public DeleteUselessLayersAction() {
		      this.putValue(Action.SHORT_DESCRIPTION,
		          "Delete all useless layers to simplify the render");
		      this.putValue(Action.NAME, "Delete layers");
		    }
	}
	
	
	/**
	   * Order the layers
	   * 
	   * @author Nicolas
	   * 
	   */
	class OrderLayersAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		
		private ArrayList<String> order;
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			fillOrderList();
			application.getMainFrame().getSelectedProjectFrame().getLayerLegendPanel().orderLayersFromBlindPlugin(order);
		}
		
		private void fillOrderList(){
			order = new ArrayList<String>();
			order.add("railwayLines");
			order.add("paths");
			order.add("sportsFields");
			order.add("squareAreas");
			order.add("cemeteries");
			order.add("buildings");
			order.add("roads");
		}

		public OrderLayersAction() {
		      this.putValue(Action.SHORT_DESCRIPTION,
		          "Order all layers to have an optimized map");
		      this.putValue(Action.NAME, "Order layers");
		    }
	}
	
	/**
	   * Save the image
	   * 
	   * @author Nicolas
	   * 
	   */
	class SaveImageAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		
		private ArrayList<String> order;
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
		//application.getMainFrame().getSelectedProjectFrame().saveAsImage("test", w, h, false);
		}


		public SaveImageAction() {
		      this.putValue(Action.SHORT_DESCRIPTION,
		          "Save the image");
		      this.putValue(Action.NAME, "Save image");
		    }
	}
}
