<?xml version="1.0" encoding = "UTF-8" standalone="yes"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
>

  <xsl:import href="utils.xsl"/>


  <xsl:param name="reformat-mode" required="yes"/>
  <xsl:param name="optimize-imports" required="yes"/>


  <xsl:template match="/project[$reformat-mode != 'null']/component[@name = 'FormatOnSaveOptions']"/>
  <xsl:template match="/project[$optimize-imports != 'null']/component[@name = 'OptimizeOnSaveOptions']"/>

  <xsl:template match="/project">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>

      <xsl:choose>
        <xsl:when test="$reformat-mode = 'WHOLE_FILE'">
          <component name="FormatOnSaveOptions">
            <option name="myRunOnSave" value="true"/>
          </component>
        </xsl:when>
        <xsl:when test="$reformat-mode = 'CHANGED_LINES'">
          <component name="FormatOnSaveOptions">
            <option name="myFormatOnlyChangedLines" value="true"/>
            <option name="myRunOnSave" value="true"/>
          </component>
        </xsl:when>
      </xsl:choose>

      <xsl:if test="$optimize-imports = 'true'">
        <component name="OptimizeOnSaveOptions">
          <option name="myRunOnSave" value="true"/>
        </component>
      </xsl:if>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
