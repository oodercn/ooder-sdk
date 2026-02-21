package net.ooder.sdk.test;

public class CoverageReport {
    private double lineCoverage;
    private double branchCoverage;
    private double methodCoverage;
    private double classCoverage;
    private int totalLines;
    private int coveredLines;
    private int totalBranches;
    private int coveredBranches;
    private int totalMethods;
    private int coveredMethods;
    private int totalClasses;
    private int coveredClasses;
    
    public double getLineCoverage() { return lineCoverage; }
    public void setLineCoverage(double lineCoverage) { this.lineCoverage = lineCoverage; }
    
    public double getBranchCoverage() { return branchCoverage; }
    public void setBranchCoverage(double branchCoverage) { this.branchCoverage = branchCoverage; }
    
    public double getMethodCoverage() { return methodCoverage; }
    public void setMethodCoverage(double methodCoverage) { this.methodCoverage = methodCoverage; }
    
    public double getClassCoverage() { return classCoverage; }
    public void setClassCoverage(double classCoverage) { this.classCoverage = classCoverage; }
    
    public int getTotalLines() { return totalLines; }
    public void setTotalLines(int totalLines) { this.totalLines = totalLines; }
    
    public int getCoveredLines() { return coveredLines; }
    public void setCoveredLines(int coveredLines) { this.coveredLines = coveredLines; }
    
    public int getTotalBranches() { return totalBranches; }
    public void setTotalBranches(int totalBranches) { this.totalBranches = totalBranches; }
    
    public int getCoveredBranches() { return coveredBranches; }
    public void setCoveredBranches(int coveredBranches) { this.coveredBranches = coveredBranches; }
    
    public int getTotalMethods() { return totalMethods; }
    public void setTotalMethods(int totalMethods) { this.totalMethods = totalMethods; }
    
    public int getCoveredMethods() { return coveredMethods; }
    public void setCoveredMethods(int coveredMethods) { this.coveredMethods = coveredMethods; }
    
    public int getTotalClasses() { return totalClasses; }
    public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }
    
    public int getCoveredClasses() { return coveredClasses; }
    public void setCoveredClasses(int coveredClasses) { this.coveredClasses = coveredClasses; }
}