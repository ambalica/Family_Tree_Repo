import java.util.ArrayList;
import java.util.List;
import java.lang.Class;

public class Person {

    private String name;
    private Gender gender;
    private List<Relation> relations;

    public Person(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
        this.relations = new ArrayList<Relation>();
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public void addParentChildRelation(ParentChild parentChild) {
        this.relations.add(parentChild);
    }

    public void addMarriageRelation(Marriage marriage) {
        this.relations.add(marriage);
    }

    public Person getSpouse() {
        Person spouse = null;
        for (Relation relation : relations) {
            if (relation.getClass() == Marriage.class) {

                // if this person is the husband, get wife
                if (((Marriage) relation).getHusband().equals(this)) {
                    spouse = ((Marriage) relation).getWife();

                } else if (((Marriage) relation).getWife().equals(this)) {
                    // if this person is the wife, get husband
                    spouse = ((Marriage) relation).getHusband();
                }
            }
        }
        return spouse;
    }

    public List<Person> getDaughters() {

        List<Person> daughters = new ArrayList<Person>();

        // Goes through all the relations of this person
        for (Relation relation : relations) {
            // If relation is a parent-child relationship
            if (relation.getClass() == ParentChild.class) {
                // If this person is parent, and child is female
                if (((ParentChild) relation).getParent().equals(this) &&
                        ((ParentChild) relation).getChild().getGender().equals(Gender.FEMALE)) {
                    // Gets the child from parent-child relation and add it to list of daughters
                    daughters.add(((ParentChild) relation).getChild());
                }
            }
        }
        return daughters;
    }
}
