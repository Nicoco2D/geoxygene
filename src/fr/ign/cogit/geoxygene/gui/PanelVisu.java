/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut G�ographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut G�ographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.event.FeatureCollectionEvent;
import fr.ign.cogit.geoxygene.feature.event.FeatureCollectionListener;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

/**
 * @author julien Gaffuri
 *
 */
public class PanelVisu extends JPanel implements Printable, ChangeListener, FeatureCollectionListener {
	private static final long serialVersionUID = 6867389438475472471L;
	private final static Logger logger=Logger.getLogger(PanelVisu.class.getName());

	private String overlayText = null;
	/**
	 * Renvoie la valeur de l'attribut overlayText.
	 * @return la valeur de l'attribut overlayText
	 */
	public String getOverlayText() {return this.overlayText;}
	/**
	 * Affecte la valeur de l'attribut overlayText.
	 * @param overlayText l'attribut overlayText � affecter
	 */
	public void setOverlayText(String overlayText) {this.overlayText = overlayText;}

	//la fenetre a laquelle le panel est eventuellement lie
	private InterfaceGeoxygene frame = null;
	public InterfaceGeoxygene getFrame() { return frame; }
	
	private DessinableGeoxygene dessinable = null;
	/**
	 * Renvoie la valeur de l'attribut dessinable.
	 * @return la valeur de l'attribut dessinable
	 */
	public DessinableGeoxygene getDessinable() {return this.dessinable;}
	
	/**
	 * coordonn�es g�ographiques du centre du panneau.
	 * ce champ determine la localisation de la zone affichee..
	 */
	public DirectPosition getCentreGeo() { return dessinable.getCentreGeo(); }
	public void setCentreGeo(DirectPosition centreGeo) {
		dessinable.setCentreGeo(centreGeo);
		update=true;
		repaint();
	}

	StyledLayerDescriptor sld;
	/**
	 * Renvoie le sld courant.
	 * @return le sld courant
	 */
	public StyledLayerDescriptor getSld() {return this.sld;}
	/**
	 * Affecte la valeur du sld courant.
	 * @param sld la valeur du sld courant � affecter
	 */
	public void setSld(StyledLayerDescriptor sld) {
		this.sld = sld;
		dessinable.setSld(sld);
		this.sld.addChangeListener(this);
	}
	StyledLayerDescriptor defaultSld;
	/**
	 * Renvoie le sld par d�faut.
	 * @return le sld par d�faut
	 */
	public StyledLayerDescriptor getDefaultSld() {return this.defaultSld;}
	/**
	 * @return l'enveloppe affich�e
	 */
	public GM_Envelope getEnveloppeAffichage() { return dessinable.getEnveloppeAffichage(); }
	public double getXMax(){ return getEnveloppeAffichage().maxX(); }
	public double getXMin(){ return getEnveloppeAffichage().minX(); }
	public double getYMax(){ return getEnveloppeAffichage().maxY(); }
	public double getYMin(){ return getEnveloppeAffichage().minY(); }
	/**
	 * taille d'un pixel (la longueur d'un cote de pixel repr�sente une longueur de taillePixel dans la r�alit�)
	 * ce champ est celui qui permet de changer le zoom de la vue
	 */
	public double getTaillePixel() { return dessinable.getTaillePixel(); }
	public void setTaillePixel(double tp) { dessinable.setTaillePixel(tp);update=true;}
    /**
     * taille d'un pixel en m (la longueur d'un cote de pixel de l'ecran)
     * utilise pour le calcul de l'echelle courante de la vue, avec 'taillePixel'
     */
    private final static double METERS_PER_PIXEL;
	public static double getMETERS_PER_PIXEL() {return METERS_PER_PIXEL;}
	static {
		//elle est calcul�e � partir de la r�solution de l'�cran en DPI.
		//par exemple si la r�solution est 90DPI, c'est: 90 pix/inch = 1/90 inch/pix = 0.0254/90 meter/pix
	    METERS_PER_PIXEL=0.02540005/Toolkit.getDefaultToolkit().getScreenResolution();
	    //System.out.print(METERS_PER_PIXEL*1280);
	}
	/**
	 * facteur multiplicatif utilise a chaque changement de zoom
	 */
	private final double facteurZoom = 1.3;
	double getFacteurZoom() { return facteurZoom; }
	/**
	 * facteur multiplicatif utilise a chaque deplacement avec les fleches du pave directionnel
	 */
	private final double facteurDeplacement = 0.2;
	double getFacteurDeplacement() { return facteurDeplacement; }
	/**
	 * les objets selectionnes
	 */
	public FT_FeatureCollection<FT_Feature> objetsSelectionnes=new FT_FeatureCollection<FT_Feature>();
	/**
	 * la couleur des objets selectionnes
	 */
	private Color COULEUR_SELECTION=new Color(255, 0, 0, 100);
	/** distance en m pour la selection */
	private double distanceSelection=1;
	public void setDistanceSelection(double d) {distanceSelection = d;}
	
	private MouseListenerGeox mlg = null;
	public MouseListenerGeox getMouseListenerGeox(){
		if(mlg == null) mlg = new MouseListenerGeox();
		return mlg;
	}

	private MouseWheelListenerGeox mwlg = null;
	public MouseWheelListenerGeox getMouseWheelListenerGeox(){
		if(mwlg == null) mwlg = new MouseWheelListenerGeox();
		return mwlg;
	}

	private KeyListenerGeox kl = null;
	public KeyListenerGeox getKeyListenerGeox(){
		if(kl == null) kl = new KeyListenerGeox();
		return kl;
	}
	
	private MouseMotionListenerGeox mmlg = null;
	public MouseMotionListenerGeox getMouseMotionListenerGeox(){
		if(mmlg == null) mmlg = new MouseMotionListenerGeox();
		return mmlg;
	}

	private ActionListenerGeox alg = null;
	public ActionListenerGeox getActionListenerGeox(){
		if(alg == null) alg = new ActionListenerGeox(this);
		return alg;
	}
	
	private PopupListenerGeox plg = null;
	public PopupListenerGeox getPopupListenerGeox(){
		if (plg==null) plg = new PopupListenerGeox(this);
		return plg;
	}

	public JPopupMenu popup = null;
	JMenuItem menuItemChargementShapefile = new JMenuItem("Charger shapefiles");
	JMenuItem menuItemCentrerVue = new JMenuItem("Centrer la vue");

	boolean useChangeListener = false;

	private DataSet dataset = null;
	/**
	 * Renvoie le DataSet
	 * @return le DataSet
	 */
	public DataSet getDataset() {return this.dataset;}

	/**
	 * Constructeur
	 * @param frameMirage la fenetre
	 */
	public PanelVisu(InterfaceGeoxygene frameMirage) {
		this.frame = frameMirage;

		addMouseListener(getMouseListenerGeox());
		addMouseWheelListener(getMouseWheelListenerGeox());
		addKeyListener(getKeyListenerGeox());
		addMouseMotionListener(getMouseMotionListenerGeox());
		/**
		 * cr�ation d'un popup menu et ajout d'un action listener
		 */
		this.popup = new JPopupMenu();
		/**
		 * Ajout des menu items au popup menu
		 */
		this.popup.add(menuItemChargementShapefile);
		menuItemChargementShapefile.addActionListener(getActionListenerGeox());
		this.popup.add(menuItemCentrerVue);
		menuItemCentrerVue.addActionListener(getActionListenerGeox());
		/**
		 * ajout d'un deuxi�me mouse listener qui ne g�re que les
		 * �v�nements venant du popupmenu 
		 */
		addMouseListener(getPopupListenerGeox());

		setFocusable(true);
		//setIgnoreRepaint(true);
		this.setDoubleBuffered(true);
				
		repaintTimer.setCoalesce(true);
		
		setBackground(Color.white);
		/** Charge le SLD par d�faut */
		defaultSld = StyledLayerDescriptor.unmarshall("defaultSLD.xml");
		/** Charge le dernier SLD utilis� */
		sld = StyledLayerDescriptor.unmarshall("sld.xml");
		
		dessinable = new DessinableGeoxygene(this.getSld());
		if (useChangeListener) dessinable.addChangeListener(this);
		sld.addChangeListener(this);
		
		this.dessinable.setSld(sld);
		
		/**
		 * Instancie un dataset
		 */
		dataset = DataSet.getInstance();
	}

	/**
	 * indique si on affiche les objet sous forme symbolisee ou non
	 */
	public boolean affichageSymbolisation = false;
	/**
	 * indique si la legende est a afficher ou non sur la vue
	 */
	private boolean affichageEchelle = false;
	/**
	 * Renvoie la valeur de l'attribut affichageEchelle.
	 * @return la valeur de l'attribut affichageEchelle
	 */
	public boolean isAffichageEchelle() {return this.affichageEchelle;}
	/**
	 * Affecte la valeur de l'attribut affichageEchelle.
	 * @param affichageEchelle l'attribut affichageEchelle � affecter
	 */
	public void setAffichageEchelle(boolean affichageEchelle) {this.affichageEchelle = affichageEchelle;}
	/**
	 * indique si la position du curseur doit �tre affich�e dans la barre du bas
	 */
	public boolean suivrePositionCurseur = false;

	/**
	 * indique si la fenetre se rafraichit automatiquement
	 */
	private boolean rafraichissementAutomatiqueActive = false;
	public boolean isRafraichissementAutomatiqueActive() {return this.rafraichissementAutomatiqueActive;}
	public void activerRafraichissementAuto() {
		rafraichissementAutomatiqueActive=true;
		if (!repaintTimer.isRunning()) {repaintTimer.start();}
	}
	
	public void desactiverRafraichissementAuto() {
		rafraichissementAutomatiqueActive=false;
		if (repaintTimer.isRunning()) {repaintTimer.stop();}		
	}

	public static int tempsRafraichissementAutomatique=400;// temps en millisecondes
	private Timer repaintTimer = new Timer(tempsRafraichissementAutomatique, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (logger.isTraceEnabled()) logger.trace("repaintTimer activ�");
			if ( (dessinable!=null) && (dessinable.getThreadMaj()!=null) && (dessinable.getThreadMaj().get()!=null) && (dessinable.getThreadMaj().get().isAlive()) ) {
				if (logger.isTraceEnabled()) logger.trace("repaintTimer copy");
				update = false;
				repaint();
				return;
			}
			//repaintTimer.stop();
			if (logger.isTraceEnabled()) logger.trace("repaintTimer stop");
			repaint();
		}
	});
	private boolean update = false;
	
	/**
	 * Effectue un zoom d'un facteur donn�
	 * @param facteur
	 */
	public void zoom(double facteur) {
		if(logger.isTraceEnabled()) logger.trace("zoom");

		/*BufferedImage imageAux = (BufferedImage)createImage(getWidth(), getHeight());
		(imageAux.getGraphics()).drawImage(getImage(), (int)(0.5*getWidth()*(1-1/facteur)), (int)(0.5*getHeight()*(1-1/facteur)), (int)(getWidth()/facteur), (int)(getHeight()/facteur), null);
		getG2D().drawImage(imageAux, 0, 0, null);
		repaint();*/
		
		dessinable.interruptMaj();

		//changement zoom
		setTaillePixel(getTaillePixel()*facteur);

		//maj limites
		//majLimitesAffichage();

		//rafraichissement
		//activer();
		update = true;
		repaint();

	}

	/**
	 * D�place le centre de la vue au niveau d'une position g�ographique donn�e.
	 * @param xGeoCentre
	 * @param yGeoCentre
	 */
	public void deplacerVue(int xGeoCentre, int yGeoCentre) {
		if(logger.isTraceEnabled()) logger.trace("deplacement");

		dessinable.interruptMaj();

		//changement position centre
		this.getCentreGeo().setX(pixToCoordX(xGeoCentre));
		this.getCentreGeo().setY(pixToCoordY(yGeoCentre));

		update = true;
		//rafraichissement
		repaint();
	}

	/**
	 * @deprecated
	 * @param obj
	 * @return
	 */
	@Deprecated
	public boolean aAfficher(FT_Feature obj) {return obj.intersecte(getEnveloppeAffichage());}

	/**
	 * met a jour le champ 'enveloppeAffichage' qui le rectangle de la fenetre de viualisation en coordonnees geographiques, en fonction des
	 * coordonnees du centre de la vue 'centreGeo' et du facteur de zoom 'taillePixel'
	 * 'enveloppeAffichage' est utilise pour determiner les objets a tracer dans la vue (ceux qui l'intersectent)
	 * @throws InterruptedException
	 */
	private synchronized void majLimitesAffichage(){
		dessinable.majLimitesAffichage(this.getWidth(),this.getHeight());
		update = false;
	}
	/**
	 * indique si l'affichage utilise l'antialiasing
	 */
	private boolean antiAliasing = true;
	/**
	 * Renvoie la valeur de l'attribut antiAliasing.
	 * @return la valeur de l'attribut antiAliasing
	 */
	public boolean isAntiAliasing() {
		return this.antiAliasing;
	}
	/**
	 * Affecte la valeur de l'attribut antiAliasing.
	 * @param antiAliasing l'attribut antiAliasing � affecter
	 */
	public void setAntiAliasing(boolean antiAliasing) {
		this.antiAliasing = antiAliasing;
		dessinable.setAntiAliasing(this.antiAliasing);
		update=true;
	}

	long start;
	@Override
	public synchronized void paintComponent(Graphics g) {
		start = System.currentTimeMillis();
		if (logger.isTraceEnabled()) logger.trace("paint()");
		Graphics2D g2 =(Graphics2D) g;
		g2.setBackground(this.getBackground());
		g2.clearRect(0,0,this.getWidth(),this.getHeight());
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAliasing?RenderingHints.VALUE_ANTIALIAS_ON:RenderingHints.VALUE_ANTIALIAS_OFF);

		if (update) {
			majLimitesAffichage();
			dessinable.start();

			if (!useChangeListener) {
				while (dessinable.getThreadMaj().get().isAlive()) {
					try {
						dessinable.getThreadMaj().get().join();
					} catch (InterruptedException e) {
						logger.error("Le thread a �t� interrompu");
						e.printStackTrace();
					}
				}
			}
		}
		copyBufferedImage(g2);
		if (rafraichissementAutomatiqueActive&&(!repaintTimer.isRunning())) {repaintTimer.start();}
		if (logger.isTraceEnabled()) logger.trace("paint() finished in "+(System.currentTimeMillis()-start));
	}
	
	/**
	 * Copie l'image du buffer dans le graphics en param�tre.
	 * @param g graphics dans lequel dessiner l'image
	 */
	public void copyBufferedImage(Graphics2D g) {
		if (logger.isTraceEnabled()) logger.trace("copy the Image");
		g.drawImage(dessinable.getImage(),0,0,null);
		/*
		try {
	        BufferedImage bi = dessinable.getImage(); // retrieve image
	        File outputfile = new File("saved.png");
	        ImageIO.write(bi, "png", outputfile);
	    } catch (IOException e) {
	    }
	    */

		//la selection
		if (getFrame().getPanelDroit().cVoirSelection.isSelected()) {
			for(FT_Feature obj : objetsSelectionnes) {
				if (obj.estSupprime()) continue;
				dessinable.dessiner(g,COULEUR_SELECTION, obj.getGeom());
			}
		}
		//la barre d'echelle
		if(affichageEchelle) try {afficherEchelle(g);} catch (InterruptedException e) {return;}
		// l'overlay s'il existe
		if(overlayText!=null) {
			/**
			 *  TODO ajouter une overlaycolor, overlaysize, overlayfont...
			 */
			g.setColor(Color.black);
			g.drawString(overlayText, 10, 10);
		}
		if (logger.isTraceEnabled()) logger.trace("paint+copyBufferedImage() finished in "+(System.currentTimeMillis()-start));
		//g2.dispose();
	}

	//les methodes de conversion entre coordonn�es �cran (pixel) et coordonn�es g�ographiques
	public int coordToPixX(double x){ return (int)((x-(getCentreGeo().getX()-getWidth()*0.5*getTaillePixel()))/getTaillePixel());}		
	public int coordToPixY(double y){ return (int)(getHeight()+(getCentreGeo().getY()-getHeight()*0.5*getTaillePixel()-y)/getTaillePixel());}
	public double pixToCoordX(int x){ return getCentreGeo().getX()-getWidth()*0.5*getTaillePixel()+x*getTaillePixel();}
	public double pixToCoordY(int y){ return getCentreGeo().getY()-getHeight()*0.5*getTaillePixel()+(getHeight()-y)*getTaillePixel();}

	/**
	 * ajuste la vue sur l'objet (position et zoom)
	 * @param obj
	 */
	public void initialiserPositionGeographique(FT_Feature obj){
		//recupere une enveloppe
		GM_Object geom = obj.getGeom().envelope().getGeom();

		//recupere les coordonnees extremes de l'enveloppe
		double xMin=geom.envelope().minX();
		double xMax=geom.envelope().maxX();
		double yMin=geom.envelope().minY();
		double yMax=geom.envelope().maxY();

		//assure que les coordonnees extreme ne sont pas confondues
		if(xMin==xMax) {xMin=xMin-10; xMax=xMax+10;}
		if(yMin==yMax) {yMin=yMin-10; yMax=yMax+10;}

		DirectPosition pt=obj.getGeom().centroid();

		//ajuste position au centre
		setCentreGeo(pt);

		//ajuste zoom
		setTaillePixel(1.2 * Math.max( (xMax-xMin)/getWidth() , (yMax-yMin)/getHeight() ));
	}

	/**
	 * centre la vue sur un objet
	 * @param obj
	 */
	public void centrer(FT_Feature obj){
		if (obj.getGeom()==null || obj.getGeom().isEmpty()) return;
		DirectPosition dr = obj.getGeom().centroid();
		deplacerVue(coordToPixX(dr.getX()), coordToPixY(dr.getY()));
	}

	/**
	 * centre la vue sur toutes les populations affich�es
	 */
	public void centrer(){
		double x=0;
		double y=0;
		int nbPop=0;
		for (Layer layer:this.getSld().getLayers()) {
			if (layer.getFeatureCollection()!=null) {
				x+=layer.getFeatureCollection().getCenter().getX();
				y+=layer.getFeatureCollection().getCenter().getY();
				nbPop++;
			}
		}
		if (nbPop>0) {
			this.setCentreGeo(new DirectPosition(x/nbPop,y/nbPop));
			if (logger.isTraceEnabled()) logger.trace("centrer sur "+x/nbPop+","+y/nbPop+" - "+nbPop);
		}
	}

	/**
	 * methode qui permet de centrer la vue sur un objet au pif contenu dans une liste de featurecollections
	 */
	public void initialiserPositionGeographique(ArrayList<Population<?>> popl){
		for(Population<?> pop : popl) {
			if(pop == null || pop.size()==0) continue;
			for(FT_Feature obj : pop) {
				if (obj.getGeom()==null || obj.getGeom().isEmpty()) continue;
				DirectPosition dr = obj.getGeom().centroid();
				setCentreGeo(dr);
				return;
			}
		}
	}

	/**
	 * ajoute a la selection les objets d'une liste proches d'un point
	 * @param liste
	 * @param pointClic
	 */
	void ajouterSelection(FT_FeatureCollection<?> liste, GM_Point pointClic) {
		//parcours des objets de la liste
		for(FT_Feature obj : liste) {
			if(obj.getGeom()==null || obj.getGeom().isEmpty()) continue;

			//si l'objet est supprime, sortir
			if(obj.estSupprime()) continue;

			//distance de la geometrie de l'objet au point du clic
			double d = obj.getGeom().distance(pointClic);

			if (d <= distanceSelection) {
				//ajout/suppression de l'objet a la selection
				if (objetsSelectionnes.contains(obj)) objetsSelectionnes.remove(obj);
				else objetsSelectionnes.add(obj);
			}
		}				
	}

	/**
	 * Affiche l'�chelle sur le graphics
	 * @param g graphics utilis� pour le dessin
	 * @throws InterruptedException
	 */
	public void afficherEchelle(Graphics2D g) throws InterruptedException{

		int nbDecalage=10;
		int nbLargeurBarre=5;
		double dist=pixToCoordX(getWidth()/3)-pixToCoordX(0);
		int log=(int) Math.log10(dist);
		dist=Math.pow(10, log);
		int nbLongeurBarre=(int) (dist/getTaillePixel());

		g.setColor(Color.BLACK);
		g.drawString(Double.toString(dist), nbDecalage+1, getHeight()-nbDecalage-nbLargeurBarre-1);
		g.fillRect(nbDecalage, getHeight()-nbDecalage-nbLargeurBarre, nbLongeurBarre, nbLargeurBarre);

		int ech=(int)(getTaillePixel()/METERS_PER_PIXEL);
		g.drawString("1:"+Integer.toString(ech), 1, 10);		
	}

	@Override
	public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
		if (pi>=1) return Printable.NO_SUCH_PAGE;

		Graphics2D g2d=(Graphics2D)g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		g2d.translate(pf.getImageableWidth() / 2, pf.getImageableHeight() / 2);
		Dimension d=getSize();

		double scale=Math.min(pf.getImageableWidth() / d.width, pf.getImageableHeight()/d.height);
		if (scale<1.0) g2d.scale(scale, scale);

		g2d.translate(-d.width / 2.0, -d.height / 2.0);
		g2d.drawImage(dessinable.getImage(), 0, 0, this);

		// TODO � v�rifier
		return Printable.PAGE_EXISTS;
	}

	/**
	 * M�thode appel�e par les objets que le panel surveille
	 * lorsque leur �tat change.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		/*
		if (e.getSource().getClass().equals(StyledLayerDescriptor.class)) repaint();
		else copyBufferedImage();
		*/
		if (logger.isTraceEnabled()) logger.trace("state changed");
		update = true;
		repaint();
	}
	/**
	 * @param couleur
	 * @param geom
	 */
	public void dessiner(Color couleur, GM_Object geom) {
		dessinable.dessiner((Graphics2D) this.getGraphics(), couleur, geom);
	}
	/**
	 * @param couleur
	 * @param geom
	 */
	public void dessinerLimite(Color couleur, GM_Polygon geom) {
		dessinable.dessiner((Graphics2D) this.getGraphics(), couleur, geom);
	}
	/**
	 * @param couleur
	 * @param geom
	 */
	public void dessinerLimite(Color couleur, GM_MultiSurface<GM_Polygon> geom) {
		dessinable.dessiner((Graphics2D) this.getGraphics(), couleur, geom);
	}
	/**
	 * @param couleur
	 * @param geom
	 * @param d
	 * @param cap
	 * @param join
	 */
	public void dessiner(Color couleur, GM_LineString geom, float d, int cap,int join) {
		dessinable.dessiner((Graphics2D) this.getGraphics(), couleur, geom, d, cap, join);
	}
	/**
	 * @param couleur
	 * @param geom
	 * @param d
	 * @param cap
	 * @param join
	 */
	public void dessiner(Color couleur, GM_MultiCurve<GM_LineString> geom, float d, int cap, int join) {
		dessinable.dessiner((Graphics2D) this.getGraphics(), couleur, geom, d, cap, join);
	}
	/**
	 * @param couleur
	 * @param geom
	 * @param d
	 * @param cap
	 * @param join
	 */
	public void dessinerLimite(Color couleur, GM_Polygon geom, float d, int cap, int join) {
		dessinable.dessiner((Graphics2D) this.getGraphics(), couleur ,geom, d, cap, join);
	}
	/**
	 * @param couleurContour
	 * @param geom
	 * @param d
	 * @param cap
	 * @param join
	 */
	@Deprecated
	public void dessinerLimite(Color couleurContour, GM_MultiSurface<GM_Polygon> geom, double d, int cap, int join) {
		dessinable.dessinerLimite((Graphics2D) this.getGraphics(), couleurContour, geom, d, cap, join);
	}
	/**
	 * @param couleur
	 * @param point
	 * @param largeur
	 */
	@Deprecated
	public void dessinerRond(Color couleur, GM_Point point, int largeur) {
		dessinable.dessinerRond((Graphics2D) this.getGraphics(), couleur, point, largeur);
	}
	/**
	 * @param couleur
	 * @param multiPoint
	 * @param largeur
	 */
	@Deprecated
	public void dessinerRond(Color couleur, GM_MultiPoint multiPoint, int largeur) {
		dessinable.dessinerRond((Graphics2D) this.getGraphics(), couleur, multiPoint, largeur);
	}
	/**
	 * @param couleur
	 * @param point
	 * @param d
	 */
	@Deprecated
	public void dessinerRond(Color couleur, GM_Point point, double d) {
		dessinable.dessinerRond((Graphics2D) this.getGraphics(), couleur, point, d);
	}
	/**
	 * @param couleur
	 * @param multiPoint
	 * @param d
	 */
	@Deprecated
	public void dessinerRond(Color couleur, GM_MultiPoint multiPoint, double d) {
		dessinable.dessinerRond((Graphics2D) this.getGraphics(), couleur, multiPoint, d);
	}
	/**
	 * @param couleur
	 * @param x
	 * @param y
	 * @param d
	 */
	@Deprecated
	public void dessinerRond(Color couleur, double x, double y, double d) {
		dessinable.dessinerRond((Graphics2D) this.getGraphics(), couleur, x, y, d);
	}
	/**
	 * @param couleur
	 * @param font
	 * @param geom
	 * @param texte
	 */
	@Deprecated
	public void dessinerTexte(Color couleur, Font font, GM_Object geom, String texte) {
		dessinable.dessinerTexte((Graphics2D) this.getGraphics(), couleur, font, geom, texte);
	}
	/**
	 * @param couleur
	 * @param geom
	 * @param texte
	 */
	@Deprecated
	public void dessinerTexte(Color couleur, GM_Object geom, String texte) {
		dessinable.dessinerTexte((Graphics2D) this.getGraphics(), couleur, geom, texte);		
	}

	/**
	 * @param couleur
	 * @param d
	 * @param e
	 * @param texte
	 */
	@Deprecated
	public void dessinerTexte(Color couleur, double d, double e, String texte) {
		dessinable.dessinerTexte((Graphics2D) this.getGraphics(), couleur, d, e, texte);		
	}
	
	/**
	 * @param coord1
	 * @param coord2
	 * @param i
	 */
	@Deprecated
	public void dessinerSegment(DirectPosition coord1, DirectPosition coord2, int i) {
		dessinable.dessinerSegment((Graphics2D) this.getGraphics(), coord1,coord2,i);
	}

	/**
	 * @param couleur
	 * @param x
	 * @param y
	 * @param d
	 * @param e
	 * @param taille
	 */
	@Deprecated
	public void dessinerSegment(Color couleur, double x, double y, double d, double e, int taille) {
		dessinable.dessinerSegment((Graphics2D) this.getGraphics(), couleur,x,y,d,e,taille);		
	}
	/**
	 * @param couleur
	 * @param x
	 * @param y
	 * @param resolution
	 */
	@Deprecated
	public void dessinerRect(Color couleur, double x, double y, int resolution) {
		dessinable.dessinerRect((Graphics2D) this.getGraphics(), couleur, x, y, resolution);
	}
	/**
	 * @param couleur
	 * @param directPosition
	 * @param taille
	 */
	@Deprecated
	public void dessiner(Color couleur, DirectPosition directPosition, int taille) {
		dessinable.dessiner((Graphics2D) this.getGraphics(), couleur, directPosition, taille);
	}
	

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		update=true;
	}
	@Override
	public void setBounds(Rectangle r) {
		super.setBounds(r);
		update=true;
	}
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		update=true;
	}
	@Override
	public void setSize(Dimension d) {
		super.setSize(d);
		update=true;
	}

	/**
	 * @param b
	 */
	public void repaint(boolean b) {
		update = update||b;
		repaint();
	}
	@Override
	public void changed(FeatureCollectionEvent event) {
		update = true;
		repaint();
	}
}
/**
 * @author julien gaffuri
 *
 */
class MouseListenerGeox implements MouseListener {
	private final static Logger logger=Logger.getLogger(MouseListenerGeox.class.getName());

	public void mousePressed(MouseEvent e) {
		if(e.getButton()==0) return;
		if(e.getButton()==MouseEvent.BUTTON1) {
			PanelVisu pv = (PanelVisu)e.getSource();
			//selection d'objets
			//position du clic
			double x = pv.pixToCoordX(e.getX()), y = pv.pixToCoordY(e.getY());
			GM_Point p = new GM_Point(new DirectPosition(x,y));

			if (pv.getSld()!=null) {
				FT_FeatureCollection<FT_Feature> selectionTotale = new FT_FeatureCollection<FT_Feature>();
				for(Layer l:pv.getSld().getLayers()) {
					if (l.getFeatureCollection()!=null) {
						FT_FeatureCollection<? extends FT_Feature> selection = l.getFeatureCollection().select(p);
						pv.ajouterSelection(selection,p);
						selectionTotale.addAll(selection);
					}
				}

				if (selectionTotale.isEmpty()) pv.objetsSelectionnes.clear();
			}

			pv.getFrame().getPanelDroit().lNbSelection.setText("Nb="+pv.objetsSelectionnes.size());
			if (! pv.isRafraichissementAutomatiqueActive()) pv.repaint();
		}
		else if(e.getButton()==MouseEvent.BUTTON2) {}
		else if(e.getButton()==MouseEvent.BUTTON3) {
			//selection de point
			//position du curseur
			/*double x=pixToCoordX(e.getX()), y=pixToCoordY(e.getY());
			double bestdistCPoint = Double.MAX_VALUE;
			double distanceSelectionC=distanceSelection*distanceSelection;
			double distC;

			//on cherche le point le plus proche du clic et a moins de DISTANCE_SELECTION pixels du clic
			for(AgentPoint p:AgentPoint.LISTE) {
				distC = (p.getX()-x)*(p.getX()-x) + (p.getY()-y)*(p.getY()-y);
				if ((distC<bestdistCPoint) && (distC<=distanceSelectionC)) {
					pointSelectionne=p;
					bestdistCPoint=distC;
				}
			}

			//si on n'a pas trouv� de point (ils sont tous trop loin du clic), on sort
			if (pointSelectionne==null) return;

			//si le point est plus proche

			//on marque le point comme selectionne
			pointSelectionne.setSelectionne(true);

			//on met le point a la position du curseur
			pointSelectionne.setX(x); pointSelectionne.setY(y);

			if (! rafraichissementAutomatique) repaint();*/
		}
		else {logger.error("clic de souris avec bouton inconnu (mousePressed): "+e.getButton());	}
	}

	public void mouseReleased(MouseEvent e) {
		if(e.getButton()==0) return;
		if(e.getButton()==MouseEvent.BUTTON1) {}
		else if(e.getButton()==MouseEvent.BUTTON2) {}
		else if(e.getButton()==MouseEvent.BUTTON3) {
			/*if (pointSelectionne!=null) {
				pointSelectionne.setX(pixToCoordX(e.getX())); pointSelectionne.setY(pixToCoordY(e.getY()));
				pointSelectionne.setSelectionne(false);
				pointSelectionne=null;
				if (! rafraichissementAutomatique) {
					try {majImage();} catch (InterruptedException e1) {}
					repaint();
				}
			}*/
		}
		else {logger.error("clic de souris avec bouton inconnu (mouseReleased): "+e.getButton());	}
	}

	public void mouseClicked(MouseEvent e) {
		PanelVisu pv = (PanelVisu)e.getSource();

		pv.requestFocus(true);

		if(e.getButton()==0) return;
		if(e.getButton()==MouseEvent.BUTTON1) {}
		else if(e.getButton()==MouseEvent.BUTTON2) {
			pv.getDessinable().interruptMaj();
			pv.deplacerVue(e.getX(), e.getY());
		}
		else if(e.getButton()==MouseEvent.BUTTON3) {
			/*
			if (pv.popup == null) {
				pv.popup = new JPopupMenu();
				JMenuItem menuItem = new JMenuItem("Charger shapefiles");
				pv.popup.add(menuItem);
				pv.popup.setLocation(e.getXOnScreen(), e.getYOnScreen());
				menuItem.addActionListener(pv.getActionListenerGeox());
			}
			pv.popup.setVisible(true);
			*/
		}
		else {logger.error("clic de souris avec bouton inconnu (mouseClicked): "+e.getButton());	}
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) { mouseReleased(e); }

}
/**
 * @author julien gaffuri
 *
 */
class MouseWheelListenerGeox implements MouseWheelListener {

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		PanelVisu pv = (PanelVisu)e.getSource();
		if (e.getWheelRotation()>0) pv.zoom(pv.getFacteurZoom());
		else pv.zoom(1/pv.getFacteurZoom());
	}
}
/**
 * @author julien gaffuri
 *
 */
class MouseMotionListenerGeox implements MouseMotionListener {

	public void mouseMoved(MouseEvent e) {
		PanelVisu pv = (PanelVisu)e.getSource();
		if (pv.suivrePositionCurseur) {
			double x=pv.pixToCoordX(e.getX()), y=pv.pixToCoordY(e.getY());
			pv.getFrame().getPanelBas().lX.setText("X="+Double.toString( Math.round((float)x*100)*0.01 ));
			pv.getFrame().getPanelBas().lY.setText("Y="+Double.toString( Math.round((float)y*100)*0.01 ));
		}
	}
	public void mouseDragged(MouseEvent e) {
	}	
}
/**
 * @author julien gaffuri
 *
 */
class KeyListenerGeox implements KeyListener {
	private final static Logger logger=Logger.getLogger(KeyListenerGeox.class.getName());

	public void keyPressed(KeyEvent e) {
		if(logger.isTraceEnabled()) logger.trace("appui sur touche du clavier "+e.getKeyCode());

		PanelVisu pv = (PanelVisu)e.getSource();

		switch(e.getKeyCode()){
		case KeyEvent.VK_SPACE : //espace: rafraichissement
			if (! pv.isRafraichissementAutomatiqueActive()) pv.repaint();
			break;
		case KeyEvent.VK_ESCAPE : //echap: vide la selection
			pv.objetsSelectionnes.clear();
			if (! pv.isRafraichissementAutomatiqueActive()) pv.repaint();
			break;
		case 33: //zoomin
			pv.zoom(1/pv.getFacteurZoom());
			break;
		case 34: //zoomout
			pv.zoom(pv.getFacteurZoom());
			break;
		case 37: //gauche
			pv.deplacerVue( (int)(pv.getWidth()*(0.5-pv.getFacteurDeplacement())), (int)(pv.getHeight()*0.5));			
			break;
		case 38: //haut
			pv.deplacerVue( (int)(pv.getWidth()*0.5), (int)(pv.getHeight()*(0.5-pv.getFacteurDeplacement())));
			break;
		case 39: //droite
			pv.deplacerVue( (int)(pv.getWidth()*(0.5+pv.getFacteurDeplacement())), (int)(pv.getHeight()*0.5));
			break;
		case 40: //bas
			pv.deplacerVue( (int)(pv.getWidth()*0.5), (int)(pv.getHeight()*(0.5+pv.getFacteurDeplacement())));
			break;
		case KeyEvent.VK_MINUS: //zoomout
			pv.zoom(pv.getFacteurZoom());
			break;
		case KeyEvent.VK_PLUS: //zoomin
			pv.zoom(1/pv.getFacteurZoom());
			break;
		case KeyEvent.VK_M: //m 
			break;
		case KeyEvent.VK_C: // centrer la vue
			pv.centrer();  
			break;
		case KeyEvent.VK_O: // charger des fichiers shape
			pv.getFrame().chargeShapefiles();  
			break;
		default:
		}
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
}
/**
 * @author Julien Perret
 *
 */
class ActionListenerGeox implements ActionListener {
	private PanelVisu panelVisu;
	public ActionListenerGeox(PanelVisu panelVisu) {this.panelVisu=panelVisu;}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==panelVisu.menuItemChargementShapefile) panelVisu.getFrame().chargeShapefiles();
		else if (e.getSource()==panelVisu.menuItemCentrerVue) panelVisu.centrer();
	}
}
/**
 * Popup listener qui g�re l'affichage du popup menu
 * @author Julien Perret
 *
 */
class PopupListenerGeox extends MouseAdapter {
	private PanelVisu panelVisu;
	public PopupListenerGeox(PanelVisu panelVisu) {this.panelVisu=panelVisu;}
    @Override
	public void mousePressed(MouseEvent e) {maybeShowPopup(e);}
    @Override
	public void mouseReleased(MouseEvent e) {maybeShowPopup(e);}
    /**
     * Affiche un menu popup si l'�v�nement souris est l'�v�nement 
     * d'affichage du menu popup.
     * @param e �v�nement souris
     */
    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            this.panelVisu.popup.show(e.getComponent(),e.getX(), e.getY());
        }
    }
}
