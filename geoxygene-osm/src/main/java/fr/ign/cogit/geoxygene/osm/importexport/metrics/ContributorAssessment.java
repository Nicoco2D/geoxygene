package fr.ign.cogit.geoxygene.osm.importexport.metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.osm.contributor.OSMContributor;
import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.schema.OSMDefaultFeature;
import fr.ign.cogit.geoxygene.osm.schema.OSMFeature;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeometryConversion;

public class ContributorAssessment {
	public static HashMap<Long, OSMContributor> contributorSummary(List<OSMResource> myJavaObjects) throws Exception {
		// Fills only the keys of myContributors hashmap
		HashMap<Long, OSMContributor> myContributors = osmContributorsInit(myJavaObjects);
		// Fills only the keys of myContributors hashmap
		// for (OSMResource resource : myJavaObjects) {
		// Long uid = (long) resource.getUid();
		// String username = resource.getContributeur();
		// if (myContributors.isEmpty() || !myContributors.containsKey(uid))
		// myContributors.put(uid,
		// new OSMContributor(new FT_FeatureCollection<OSMFeature>(), username,
		// uid.intValue()));
		// }

		Iterator<Long> contributorsIDs = myContributors.keySet().iterator();
		while (contributorsIDs.hasNext()) {
			long currentUID = contributorsIDs.next();
			List<OSMFeature> contributionList = fillcontributionList(currentUID, myJavaObjects);
			for (OSMFeature contribution : contributionList)
				myContributors.get(currentUID).addContribution(contribution);

			// long currentID = objectIDs.next();
			// List<OSMResource> contributionList =
			// fillcontributionList("OSMNode", currentID, myOSMNodeObjects);
			// intrinsicIndicators(currentID, contributionList,
			// myOSMNodeObjects);
			// List<String> contributorList = contributorList("OSMNode",
			// currentID);
			// myOSMNodeObjects.get(currentID).setNbContributors(contributorList.size());

			/** Calcul des indicateurs **/
			int nbOfContributions = myContributors.get(currentUID).getContributions().size();
			int nbOfDayTimeContributions = myContributors.get(currentUID).getDaytimeContributions().size();
			int nbOfNightTimeContributions = myContributors.get(currentUID).getNighttimeContributions().size();
			int nbOfweekContributions = myContributors.get(currentUID).getWeekContributions().size();
			int nbOfweekendContributions = myContributors.get(currentUID).getWeekEndContributions().size();
			myContributors.get(currentUID).setNbOfContributions(nbOfContributions);
			myContributors.get(currentUID).setNbOfDayTimeContributions(nbOfDayTimeContributions);
			myContributors.get(currentUID).setNbOfNightTimeContributions(nbOfNightTimeContributions);
			myContributors.get(currentUID).setNbOfweekContributions(nbOfweekContributions);
			myContributors.get(currentUID).setNbOfweekendContributions(nbOfweekendContributions);
		}

		// for (Long uid : myContributors.keySet()) {
		// for (OSMResource resource : myJavaObjects) {
		// if (resource.getUid() == uid) {
		// OSMFeature feature = OSMResource2OSMFeature(resource, myJavaObjects);
		// myContributors.get(uid).addContribution(feature);
		// }
		// }
		// /** Calcul des indicateurs **/
		// int nbOfContributions =
		// myContributors.get(uid).getContributions().size();
		// int nbOfDayTimeContributions =
		// myContributors.get(uid).getDaytimeContributions().size();
		// int nbOfNightTimeContributions =
		// myContributors.get(uid).getNighttimeContributions().size();
		// int nbOfweekContributions =
		// myContributors.get(uid).getWeekContributions().size();
		// int nbOfweekendContributions =
		// myContributors.get(uid).getWeekEndContributions().size();
		// myContributors.get(uid).setNbOfContributions(nbOfContributions);
		// myContributors.get(uid).setNbOfDayTimeContributions(nbOfDayTimeContributions);
		// myContributors.get(uid).setNbOfNightTimeContributions(nbOfNightTimeContributions);
		// myContributors.get(uid).setNbOfweekContributions(nbOfweekContributions);
		// myContributors.get(uid).setNbOfweekendContributions(nbOfweekendContributions);
		// }
		return myContributors;
	}

	public static List<OSMFeature> fillcontributionList(Long currentUID, List<OSMResource> myJavaObjects)
			throws Exception {
		// Select all the java objects that contain currentID
		List<OSMFeature> contributionList = new ArrayList<OSMFeature>();
		// System.out.println("taille de myJavaObjetcs : " +
		// myJavaObjects.size());
		// System.out.println("myJavaObjects.get(0).getContributeur() : " +
		// myJavaObjects.get(0).getContributeur());
		for (int i = 0; i < myJavaObjects.size(); i++) {
			if (currentUID == myJavaObjects.get(i).getUid()) {
				OSMFeature feature = OSMResource2OSMFeature(myJavaObjects.get(i), myJavaObjects);
				contributionList.add(feature);
			}
		}
		return contributionList;
	}

	public static HashMap<Long, OSMContributor> osmContributorsInit(List<OSMResource> myJavaObjects) {
		// Sorting OSMResource list in the chronological order
		IntrinsicAssessment.sortJavaObjects(myJavaObjects);
		HashMap<Long, OSMContributor> myContributors = new HashMap<Long, OSMContributor>();
		/** Parsing myJavaObjects to create indicators inside of OSMObjects **/
		Iterator<OSMResource> it = myJavaObjects.iterator();
		while (it.hasNext()) {
			OSMResource contribution = it.next();
			Long uid = (long) contribution.getUid();
			String username = contribution.getContributeur();
			if (!myContributors.containsKey(uid)) {
				myContributors.put(uid,
						new OSMContributor(new FT_FeatureCollection<OSMFeature>(), username, uid.intValue()));
			}

		}
		return myContributors;
	}

	public static OSMFeature OSMResource2OSMFeature(OSMResource resource, List<OSMResource> myJavaObjects)
			throws Exception {
		OsmGeometryConversion convertor = new OsmGeometryConversion("4326");
		IGeometry igeom = null;
		if (resource.getGeom().getClass().getSimpleName().equals("OSMNode")) {
			igeom = convertor.convertOsmPoint((OSMNode) resource.getGeom());
		}
		if (resource.getGeom().getClass().getSimpleName().equals("OSMWay")) {
			List<OSMResource> myNodes = IntrinsicAssessment.getNodesComposingWay(myJavaObjects, resource);
			igeom = convertor.convertOSMLine((OSMWay) resource.getGeom(), myNodes);
		}
		if (resource.getGeom().getClass().getSimpleName().equals("OSMRelation")) {
			// Plus tard
		}
		OSMFeature feature = new OSMDefaultFeature(resource.getContributeur(), igeom, (int) resource.getId(),
				resource.getChangeSet(), resource.getVersion(), resource.getUid(), resource.getDate(),
				resource.getTags());
		return feature;
	}

	public static void writeContributorSummary(HashMap<Long, OSMContributor> myContributors, File file)
			throws IOException {
		// Create a CSV writer
		CSVWriter writer = new CSVWriter(new FileWriter(file), ';');
		// write header
		String[] line = new String[7];
		line[0] = "uid";
		line[1] = "name";
		line[2] = "nbContributions";
		line[3] = "nbDayContributions";
		line[4] = "nbNightContributions";
		line[5] = "nbOfweekContributions";
		line[6] = "nbOfweekendContributions";
		writer.writeNext(line);
		for (OSMContributor contributor : myContributors.values()) {
			line = new String[7];
			line[0] = String.valueOf(contributor.getId());
			line[1] = String.valueOf(contributor.getName());
			line[2] = String.valueOf(contributor.getNbOfContributions());
			line[3] = String.valueOf(contributor.getNbOfDayTimeContributions());
			line[4] = String.valueOf(contributor.getNbOfNightTimeContributions());
			line[5] = String.valueOf(contributor.getNbOfweekContributions());
			line[6] = String.valueOf(contributor.getNbOfweekendContributions());
			writer.writeNext(line);
		}
		writer.close();
	}
}
