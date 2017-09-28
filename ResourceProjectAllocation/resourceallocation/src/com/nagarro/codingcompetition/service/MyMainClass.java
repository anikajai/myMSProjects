package com.nagarro.codingcompetition.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.nagarro.codingcompetition.openings.ResourceData;
import com.nagarro.codingcompetition.pojo.Opening;
import com.nagarro.codingcompetition.pojo.Project;
import com.nagarro.codingcompetition.pojo.Resource;

public class MyMainClass {
	private static List<Opening> openings = new ArrayList<Opening>();
	private static List<Resource> resources = new ArrayList<Resource>();
	private static Map<String, Project> projectAllocationDataMap = new HashMap<String, Project>();

	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		com.nagarro.codingcompetition.resources.ResourceData resourceSet = null;
		ResourceData openingSet = null;
		try {
			File file = new File("openings.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(ResourceData.class);
			File file1 = new File("resources.xml");
			JAXBContext jaxbContext1 = JAXBContext
					.newInstance(com.nagarro.codingcompetition.resources.ResourceData.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			openingSet = (ResourceData) jaxbUnmarshaller.unmarshal(file);
			Unmarshaller jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
			resourceSet = (com.nagarro.codingcompetition.resources.ResourceData) jaxbUnmarshaller1.unmarshal(file1);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		OpeningsResourcePreprocessor.mapper(openings, resources, openingSet, resourceSet);
//		openings = openings.subList(0, 20);
//		resources = resources.subList(0, 20);
		OpeningsResourcePreprocessor.openingsProcessor(openings);
		OpeningsResourcePreprocessor.resourcesProcessor(resources);
		AllotmentService.matchResourceOpening(openings, resources);
		AllotmentService.changeMatchScore(openings);
		ProjectOpeningsDataIntegrator.projectOpeningsIntegration(openings, projectAllocationDataMap);
		AllotmentService.rejectBasedOnKeyConstraints(openings);
		int numOfOpenings = openings.size();
		AllotmentService.createMatrix(resources, openings, numOfOpenings);
		AllotmentService.optimisationHeuristic(resources, openings);
		System.out.println(System.currentTimeMillis());
	}

}
