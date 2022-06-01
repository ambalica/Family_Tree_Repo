import java.util.*;

public class Family {

    private Map<String, Person> familyTree;

    public Family() {
        this.familyTree = new HashMap<String, Person>();
    }

    public Person getMember(String name) {
        return familyTree.get(name);
    }

    public void addMember(Person member) {
        if (!familyTree.containsKey(member.getName())) {
            familyTree.put(member.getName(), member);
        }
    }

    public Person addChild(Person parent, Person child) {

        // Creates a new parent child relation between child and the parent
        ParentChild parentChildRelation = new ParentChild(parent, child);
        child.addParentChildRelation(parentChildRelation);
        parent.addParentChildRelation(parentChildRelation);

        // Creates a new parent child relation between child and parent's spouse
        if (parent.getSpouse() != null) {
            ParentChild spouseParentChildRelation = new ParentChild(parent.getSpouse(), child);
            child.addParentChildRelation(spouseParentChildRelation);
            parent.getSpouse().addParentChildRelation(spouseParentChildRelation);
        }

        // Adds parent(if not already in) and child to family tree
        addMember(parent);
        addMember(child);

        return child;
    }

    public void addMarriage(Person spouse1, Person spouse2) {
        // Instantiate marriage object
        Marriage marriage;

        if (spouse1.getGender().equals(Gender.FEMALE)) {
            // As it is marriage(husband,wife) so we change order
            marriage = new Marriage(spouse2, spouse1);
        } else {
            marriage = new Marriage(spouse1, spouse2);
        }

        // Adding marriage relation to both husband and wife
        marriage.getHusband().addMarriageRelation(marriage);
        marriage.getWife().addMarriageRelation(marriage);

        addMember(marriage.getHusband());
        addMember(marriage.getWife());
    }

    //Problem - Find mother having the highest number of daughters
    public List<Person> getMotherWithMostDaughters() {

        List<Person> mothersWithMostDaughters = new ArrayList<Person>();
        int maxDaughters = 0;

        for (Person member : familyTree.values()) {

            // Find mother
            if (member.getGender().equals(Gender.FEMALE)) {

                // Find all the daughters of this mother
                List<Person> daughtersList = member.getDaughters();

                if (daughtersList != null && daughtersList.size() > 0) {
                    // If this mother's daughters are the maximum number of daughters
                    if (daughtersList.size() == maxDaughters) {
                        mothersWithMostDaughters.add(member);
                    } else if (daughtersList.size() > maxDaughters) {
                        // If this mother's daughters are higher
                        // than the current maximum number of daughters
                        maxDaughters = daughtersList.size();
                        mothersWithMostDaughters.clear();
                        mothersWithMostDaughters.add(member);
                    }
                }
            }
        }
        return mothersWithMostDaughters;
    }

    //Problem - Use BFS to find the shortest path of relations from source person to destination person
    public String getShortestRelation(Person source, Person dest) {

        Map<Person, Boolean> isVisited = new HashMap<Person, Boolean>();
        Map<Person, Relation> path = new HashMap<Person, Relation>();

        Queue<Person> personQueue = new LinkedList<Person>();

        // Initialise the isVisited to false
        for (Person p : familyTree.values()) {
            isVisited.put(p, false);
        }

        //Add source node to queue
        personQueue.add(source);
        isVisited.put(source, true);

        // Find path while the person queue is not empty
        while (!personQueue.isEmpty()) {
            // Get the current person
            Person current = personQueue.remove();

            // The loop breaks once we reach the person2 from person1
            if (current.equals(dest))
                break;

            // Look at current person's neighbours
            List<Relation> relations = current.getRelations();

            for (Relation relation : relations) {

                if (relation.getClass() == ParentChild.class) {

                    ParentChild parentChildRelation = (ParentChild) relation;

                    // If current person is a parent, add the child to personQueue
                    if (parentChildRelation.getParent().equals(current)) {
                        // Process the child
                        processPersonInQueue(parentChildRelation.getChild(), relation, isVisited, path, personQueue);
                    } else if (((ParentChild) relation).getChild().equals(current)) {
                        // If person is a child, add the parent to personQueue
                        processPersonInQueue(((ParentChild) relation).getParent(), relation, isVisited, path, personQueue);
                    }
                } else if (relation.getClass() == Marriage.class) {

                    Marriage marriageRelation = (Marriage) relation;

                    // If current person is a spouse
                    if (marriageRelation.getHusband().equals(current)) {
                        // Add the wife to personQueue
                        processPersonInQueue(((Marriage) relation).getWife(), relation, isVisited, path, personQueue);
                    } else if (marriageRelation.getWife().equals(current)) {
                        // Add the husband to personQueue
                        processPersonInQueue(((Marriage) relation).getHusband(), relation, isVisited, path, personQueue);
                    }
                }
            }
        }

        // Now we know the path between the two people
        List<RelationType> relationList = getRelationListFromPath(path, source, dest);
        // Name the relationship between the two people
        return getRelationshipName(relationList);

    }

    private void processPersonInQueue(Person p, Relation relation, Map<Person, Boolean> isVisited, Map<Person, Relation> path, Queue<Person> personQueue) {
        if (!isVisited.get(p)) {
            personQueue.add(p);
            isVisited.put(p, true);
            path.put(p, relation);
        }
    }

    private List<RelationType> getRelationListFromPath(Map<Person, Relation> path, Person source, Person dest) {

        List<RelationType> relationList = new ArrayList<RelationType>();

        //Generate list of relations from path from destination to source
        for (Person person = dest; person != source; ) {

            if (path.get(person).getClass() == ParentChild.class) {

                // If person is the child
                if (((ParentChild) path.get(person)).getChild().equals(person)) {
                    if (person.getGender().equals(Gender.FEMALE)) {
                        relationList.add(RelationType.DAUGHTER);
                    } else {
                        relationList.add(RelationType.SON);
                    }
                    // Get parent as the person
                    person = ((ParentChild) path.get(person)).getParent();
                } else if (((ParentChild) path.get(person)).getParent().equals(person)) {

                    // If person is a parent
                    if (person.getGender().equals(Gender.FEMALE)) {
                        relationList.add(RelationType.MOTHER);
                    } else {
                        relationList.add(RelationType.FATHER);
                    }
                    // Get the child as the person
                    person = ((ParentChild) path.get(person)).getChild();
                }
            } else if (path.get(person).getClass() == Marriage.class) {

                // If person is a wife
                if (((Marriage) path.get(person)).getWife().equals(person)) {
                    relationList.add(RelationType.WIFE);
                    // Get husband as the person
                    person = ((Marriage) path.get(person)).getHusband();

                } else if (((Marriage) path.get(person)).getHusband().equals(person)) {
                    // If person is a husband
                    relationList.add(RelationType.HUSBAND);
                    // Get wife as the person
                    person = ((Marriage) path.get(person)).getWife();
                }
            }
        }

        //Reverse list to get order from source to dest
        Collections.reverse(relationList);
        return relationList;
    }

    private String getRelationshipName(List<RelationType> relationList) {
        String relationships = "|";
        for (RelationType relationType : relationList) {
            relationships += relationType.toString() + "|";
        }

        //Derive simplified relation names
        for(Map.Entry<String, String> entry: getRelationMap().entrySet()){
            relationships=relationships.replace(entry.getKey(), entry.getValue());
        }

        //Remove delimiters and convert it to 'S
        relationships = relationships.substring(1, relationships.length() - 1);
        relationships = relationships.replace("|", "'S ");
        return relationships;
    }

    private Map<String, String> getRelationMap() {

        Map<String, String> relationMap = new LinkedHashMap<String, String>();
        relationMap.put("|MOTHER|SON|", "|BROTHER|");
        relationMap.put("|FATHER|SON|", "|BROTHER|");
        relationMap.put("|MOTHER|DAUGHTER|", "|SISTER|");
        relationMap.put("|FATHER|DAUGHTER|", "|SISTER|");
        relationMap.put("|SON|SON|", "|GRANDSON|");
        relationMap.put("|DAUGHTER|SON|", "|GRANDSON|");
        relationMap.put("|SON|DAUGHTER|", "|GRANDDAUGHTER|");
        relationMap.put("|DAUGHTER|DAUGHTER|", "|GRANDDAUGHTER|");
        relationMap.put("|FATHER|FATHER|", "|GRANDFATHER|");
        relationMap.put("|MOTHER|FATHER|", "|GRANDFATHER|");
        relationMap.put("|FATHER|MOTHER|", "|GRANDMOTHER|");
        relationMap.put("|MOTHER|MOTHER|", "|GRANDMOTHER|");
        relationMap.put("|FATHER|BROTHER|SON|", "|COUSIN|");
        relationMap.put("|FATHER|SISTER|SON|", "|COUSIN|");
        relationMap.put("|FATHER|BROTHER|DAUGHTER|", "|COUSIN|");
        relationMap.put("|FATHER|SISTER|DAUGHTER|", "|COUSIN|");
        relationMap.put("|MOTHER|BROTHER|SON|", "|COUSIN|");
        relationMap.put("|MOTHER|SISTER|SON|", "|COUSIN|");
        relationMap.put("|MOTHER|BROTHER|DAUGHTER|", "|COUSIN|");
        relationMap.put("|MOTHER|SISTER|DAUGHTER|", "|COUSIN|");
        relationMap.put("|WIFE|BROTHER|", "|BROTHER-IN-LAW|");
        relationMap.put("|HUSBAND|BROTHER|", "|BROTHER-IN-LAW|");
        relationMap.put("|SISTER|HUSBAND|", "|BROTHER-IN-LAW|");
        relationMap.put("|HUSBAND|SISTER|", "|SISTER-IN-LAW|");
        relationMap.put("|WIFE|SISTER|", "|SISTER-IN-LAW|");
        relationMap.put("|BROTHER|WIFE|", "|SISTER-IN-LAW|");
        relationMap.put("|MOTHER|SISTER|", "|MATERNAL AUNT|");
        relationMap.put("|MOTHER|SISTER-IN-LAW|", "|MATERNAL AUNT|");
        relationMap.put("|FATHER|SISTER|", "|PATERNAL AUNT|");
        relationMap.put("|FATHER|SISTER-IN-LAW|", "|PATERNAL AUNT|");
        relationMap.put("|MOTHER|BROTHER|", "|MATERNAL UNCLE|");
        relationMap.put("|MOTHER|BROTHER-IN-LAW|", "|MATERNAL UNCLE|");
        relationMap.put("|FATHER|BROTHER|", "|PATERNAL UNCLE|");
        relationMap.put("|FATHER|BROTHER-IN-LAW|", "|PATERNAL UNCLE|");

        return relationMap;
    }
}
