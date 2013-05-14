package com.github.dprentiss.reqs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;


/**
 * Utility for creating test databases for Reqs.
 *
 * @author David Prentiss
 */
public class TestDbFactory {
    private static Random rand = new Random();

    public static void create(ReqsDb testDb) {
        List<PrimaryEntity> entities = new ArrayList<PrimaryEntity>();
        String[] names = {
            "Binyam Abeye",
            "Chris Binkley",
            "Sijia Cao",
            "Peter Linnehan",
            "Apurv Mittal",
            "Iris Mu",
            "Alan Nguyen",
            "David Prentiss",
            "Liang Qiao",
            "James Vaughn",
            "Hsi-Hsien Wei",
            "Mark Austin",
            "UMD",
            "ENCE688R",
            "ISR",
            "CEE",
            "Civil Systems",
            "Team A",
            "Team B"
        };

        // create some primary entities from String[] names
        for (int i = 0; i < names.length; i++) {
            PrimaryEntity newEntity = 
                new PrimaryEntity(testDb.createPrimaryEntity());
            newEntity.setName(names[i]);
            entities.add(newEntity);
        }

        // add some memberships
        for (int i = 0; i < entities.size(); i++) {
            entities.get(12).addMember(entities.get(i));
        }

        for (int i = 0; i < 12; i++) {
            entities.get(13).addMember(entities.get(i));
        }

        for (int i = 8; i < 12; i++) {
            entities.get(14).addMember(entities.get(i));
        }

        for (int i = 0; i < 8; i++) {
            entities.get(15).addMember(entities.get(i));
        }

        for (int i = 5; i < 8; i++) {
            entities.get(17).addMember(entities.get(i));
        }

        for (int i = 0; i < 5; i++) {
            entities.get(18).addMember(entities.get(i));
        }

        entities.get(16).addMember(entities.get(7));
        entities.get(15).addMember(entities.get(16));


        // add some memberships
        Person Dave = new Person(testDb.getPrimaryEntity("David Prentiss"));
        PrimaryEntity TeamA = 
            new PrimaryEntity(testDb.getPrimaryEntity("Team A"));
        TeamA.addMember(Dave);
    
        // Some URI's for documents
        String[] uris = {
            "ftp://example.org/concern01.txt",
            "ftp://example.org/concern02.txt",
            "ftp://example.org/concern03.txt",
            "ftp://example.org/concern04.txt",
            "ftp://example.org/concern05.txt",
            "http://example.org/concern01.txt",
            "http://example.org/concern02.txt",
            "http://example.org/concern03.txt",
            "http://example.org/concern04.txt",
            "http://example.org/concern05.txt",
            "ftp://umd.edu/concern01.txt",
            "ftp://umd.edu/concern02.txt",
            "ftp://umd.edu/concern03.txt",
            "ftp://umd.edu/concern04.txt",
            "ftp://umd.edu/concern05.txt",
            "http://umd.edu/concern01.txt",
            "http://umd.edu/concern02.txt",
            "http://umd.edu/concern03.txt",
            "http://umd.edu/concern04.txt",
            "http://umd.edu/concern05.txt",
            "ftp://example.org/project/concern01.txt",
            "ftp://example.org/project/concern02.txt",
            "ftp://example.org/project/concern03.txt",
            "ftp://example.org/project/concern04.txt",
            "ftp://example.org/project/concern05.txt",
            "http://example.org/project/concern01.txt",
            "http://example.org/project/concern02.txt",
            "http://example.org/project/concern03.txt",
            "http://example.org/project/concern04.txt",
            "http://example.org/project/concern05.txt",
            "ftp://umd.edu/project/concern01.txt",
            "ftp://umd.edu/project/concern02.txt",
            "ftp://umd.edu/project/concern03.txt",
            "ftp://umd.edu/project/concern04.txt",
            "ftp://umd.edu/project/concern05.txt",
            "http://umd.edu/project/concern01.txt",
            "http://umd.edu/project/concern02.txt",
            "http://umd.edu/project/concern03.txt",
            "http://umd.edu/project/concern04.txt",
            "http://umd.edu/project/concern05.txt",
            "ftp://example.org/project/viewpoint01.txt",
            "ftp://example.org/project/viewpoint02.txt",
            "ftp://example.org/project/viewpoint03.txt",
            "ftp://example.org/project/viewpoint04.txt",
            "ftp://example.org/project/viewpoint05.txt",
            "http://example.org/project/viewpoint01.txt",
            "http://example.org/project/viewpoint02.txt",
            "http://example.org/project/viewpoint03.txt",
            "http://example.org/project/viewpoint04.txt",
            "http://example.org/project/viewpoint05.txt",
            "ftp://umd.edu/project/viewpoint01.txt",
            "ftp://umd.edu/project/viewpoint02.txt",
            "ftp://umd.edu/project/viewpoint03.txt",
            "ftp://umd.edu/project/viewpoint04.txt",
            "ftp://umd.edu/project/viewpoint05.txt",
            "http://umd.edu/project/viewpoint01.txt",
            "http://umd.edu/project/viewpoint02.txt",
            "http://umd.edu/project/viewpoint03.txt",
            "http://umd.edu/project/viewpoint04.txt",
            "http://umd.edu/project/viewpoint05.txt"
        };

        // create some documents
        List<Document> docs = new ArrayList<Document>();
        for (int i = 0; i < uris.length; i++) {
            Document newDocument = 
                new Document(testDb.createDocument());
            newDocument.setURI(uris[i]);
            newDocument.setSummary("Lorem ipsum dolor sit amet.");
            docs.add(newDocument);
        }

        // create some stakeholders and concerns by connecting
        // primary entities to documents with the IDENTIFIES relationship
        int numPE;
        for (Document d : docs) {
            if (d.getURI().toLowerCase().contains("concern")) {
                Concern newConcern = new Concern(d.getNode());
                numPE = rand.nextInt(4) + 1;
                for (int i = 0; i < numPE; i++) {
                    newConcern.addStakeholder(
                            entities.get(rand.nextInt(entities.size())));
                }
            }
        }
    }
}
