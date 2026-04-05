package net.ooder.bpm.client;

public class SelectPerson {

    public SelectPerson() {

    }

    Perform performs = new Perform();

    Perform readers = new Perform();

    Perform insteadSigns = new Perform();

    public Perform getPerforms() {
        return performs;
    }

    public void setPerforms(Perform performs) {
        this.performs = performs;
    }

    public Perform getReaders() {
        return readers;
    }

    public void setReaders(Perform readers) {
        this.readers = readers;
    }


    public Perform getInsteadSigns() {
        return insteadSigns;
    }

    public void setInsteadSigns(Perform insteadSigns) {
        this.insteadSigns = insteadSigns;
    }
}
