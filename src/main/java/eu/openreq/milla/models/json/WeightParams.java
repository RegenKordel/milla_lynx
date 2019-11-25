package eu.openreq.milla.models.json;

public class WeightParams {

    private double orphanFactor;

    private int minimumDistance;
    private double minDistanceFactor;

    private String projectId;
    private double projectFactor;

    private int dateDifference;
    private double dateFactor;

    private String componentName;
    private double componentFactor;

    private String labelName;
    private double labelFactor;

    private String platformName;
    private double platformFactor;

    public double getOrphanFactor() {
        return orphanFactor;
    }

    public void setOrphanFactor(double orphanFactor) {
        this.orphanFactor = orphanFactor;
    }

    public int getMinimumDistance() {
        return minimumDistance;
    }

    public void setMinimumDistance(int minimumDistance) {
        this.minimumDistance = minimumDistance;
    }

    public double getMinDistanceFactor() {
        return minDistanceFactor;
    }

    public void setMinDistanceFactor(double minDistanceFactor) {
        this.minDistanceFactor = minDistanceFactor;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public double getProjectFactor() {
        return projectFactor;
    }

    public void setProjectFactor(double projectFactor) {
        this.projectFactor = projectFactor;
    }

    public int getDateDifference() {
        return dateDifference;
    }

    public void setDateDifference(int dateDifference) {
        this.dateDifference = dateDifference;
    }

    public double getDateFactor() {
        return dateFactor;
    }

    public void setDateFactor(double dateFactor) {
        this.dateFactor = dateFactor;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public double getComponentFactor() {
        return componentFactor;
    }

    public void setComponentFactor(double componentFactor) {
        this.componentFactor = componentFactor;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public double getLabelFactor() {
        return labelFactor;
    }

    public void setLabelFactor(double labelFactor) {
        this.labelFactor = labelFactor;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public double getPlatformFactor() {
        return platformFactor;
    }

    public void setPlatformFactor(double platformFactor) {
        this.platformFactor = platformFactor;
    }
}
