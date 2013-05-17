reqs
====

Requirements Engineering Query System
''' java
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
''' java
