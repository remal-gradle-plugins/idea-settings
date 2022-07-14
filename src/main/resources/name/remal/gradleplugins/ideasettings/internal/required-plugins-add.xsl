<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
>

  <xsl:import href="utils.xsl"/>


  <xsl:param name="plugin-ids" required="yes"/>


  <xsl:template match="/project">
    <xsl:call-template name="ensure-project-component">
      <xsl:with-param name="component-name" select="'ExternalDependencies'"/>
    </xsl:call-template>
  </xsl:template>


  <xsl:template match="/project/component[@name = 'ExternalDependencies']">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>

      <xsl:variable name="already-added-plugin-ids" select="plugin/@id"/>
      <xsl:for-each select="$plugin-ids">
        <xsl:if test="not(index-of($already-added-plugin-ids, current()))">
          <plugin id="{current()}"/>
        </xsl:if>
      </xsl:for-each>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
