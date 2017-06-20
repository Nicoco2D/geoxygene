package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.io.InputStream;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
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
	    
	    JMenuItem menuItemBrailleName = new JMenuItem(itemBrailleName);
	    menuItemBrailleName.addActionListener(this);
	    
	    JMenuItem menuItemGreyLevelMap = new JMenuItem(itemGreyLevelMap);
	    menuItemGreyLevelMap.addActionListener(this);
	    
	    JMenuItem menuItemParameters = new JMenuItem(itemParameters);
	    menuItemParameters.addActionListener(this);
	    
	    blindMenu.add(menuItemBrailleName);
	    blindMenu.add(menuItemGreyLevelMap);
	    blindMenu.addSeparator();
	    blindMenu.add(menuItemParameters);

	    
	    menuBar.add(blindMenu, menuBar.getMenuCount() - 1);

	  }
	  
	  @SuppressWarnings("unchecked")
	  @Override
	  public void actionPerformed(final ActionEvent e) {

		  if(e.getActionCommand() == itemBrailleName){
			  System.out.println("Noms en braille");
			  actionItemBrailleName();
		  } else if (e.getActionCommand() == itemGreyLevelMap){
			  System.out.println("Map en niveau de gris");
			  actionItemGreyLevelMap();
		  } else if (e.getActionCommand() == itemParameters){
			  System.out.println("Parametres");
			  actionItemParameters();
		  }
		  
	  }



	private void actionItemBrailleName() {
		     
	}
	

	private void actionItemGreyLevelMap() {
		
	}
	
	private void actionItemParameters() {
		
	}
	


}
