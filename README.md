Requirements Engineerinng Query System
======================================

Reqs is a proof-of-concept application that demonstrates the ability of graph- based databases to model and query complex entity-relationship concepts. In this case, an embedded database based on Neo4j is used to represent Stakeholders, Concerns, and Viewpoints as described in IEEE 1471 standard describing the architecture of a software-intensive system. Although the application at present is particular to architecture description, it is being deloped with full-lifecycle, general systems in mind.  

The challenge presented by this task is to capture and effectively traverse the many-to-many relationships between the entities. For this initial version of Reqs, the user is presented with a graph-based visual representation of the Stakeholders, Concerns, and Viewpoints in a hypothetical architecture description. The user can graphically select one or more entities and the system responds by highlighting the entities that are related to it.  

Getting Started
---------------




``` java
 public Iterable<Relationship> getRelationships() {
        TraversalDescription traversal = Traversal.description()
            .breadthFirst()
            .evaluator(new Evaluator() {
                @Override
                public Evaluation evaluate(final Path path) {
                    if (path.length() == 0) {
                        return Evaluation.EXCLUDE_AND_CONTINUE;
                    }
                    boolean isOutgoingIS_MEMBER = (
                        path.lastRelationship().getEndNode() == 
                        path.endNode() &&
                        (path.lastRelationship()
                        .isType(ReqsDb.RelTypes.IS_MEMBER)));
                    boolean isRelTypeUnique = true;
                    Iterator<Relationship> i = 
                path.reverseRelationships().iterator();
                    i.next();
                    while (i.hasNext()) {
                        if (i.next().isType(path.lastRelationship()
                                .getType())) {
                            isRelTypeUnique = false;
                            break;
                        }
                    }
                    boolean included = isOutgoingIS_MEMBER || isRelTypeUnique;
                    boolean continued = included;
                    return Evaluation.of(included, continued);
                }
            });
```
