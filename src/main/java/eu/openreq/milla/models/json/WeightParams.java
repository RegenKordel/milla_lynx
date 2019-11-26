package eu.openreq.milla.models.json;

public class WeightParams {

    private Double orphanFactor;

    private Integer minimumDistance;
    private Double minDistanceFactor;

    private String projectId;
    private Double projectFactor;

    private Integer dateDifference;
    private Double dateFactor;

    private String componentName;
    private Double componentFactor;

    private String labelName;
    private Double labelFactor;

    private String platformName;
    private Double platformFactor;

    public Double getOrphanFactor() {
        return orphanFactor;
    }

    public void setOrphanFactor(Double orphanFactor) {
        this.orphanFactor = orphanFactor;
    }

    public Integer getMinimumDistance() {
        return minimumDistance;
    }

    public void setMinimumDistance(Integer minimumDistance) {
        this.minimumDistance = minimumDistance;
    }

    public Double getMinDistanceFactor() {
        return minDistanceFactor;
    }

    public void setMinDistanceFactor(Double minDistanceFactor) {
        this.minDistanceFactor = minDistanceFactor;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public Double getProjectFactor() {
        return projectFactor;
    }

    public void setProjectFactor(Double projectFactor) {
        this.projectFactor = projectFactor;
    }

    public Integer getDateDifference() {
        return dateDifference;
    }

    public void setDateDifference(Integer dateDifference) {
        this.dateDifference = dateDifference;
    }

    public Double getDateFactor() {
        return dateFactor;
    }

    public void setDateFactor(Double dateFactor) {
        this.dateFactor = dateFactor;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Double getComponentFactor() {
        return componentFactor;
    }

    public void setComponentFactor(Double componentFactor) {
        this.componentFactor = componentFactor;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public Double getLabelFactor() {
        return labelFactor;
    }

    public void setLabelFactor(Double labelFactor) {
        this.labelFactor = labelFactor;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public Double getPlatformFactor() {
        return platformFactor;
    }

    public void setPlatformFactor(Double platformFactor) {
        this.platformFactor = platformFactor;
    }
}
