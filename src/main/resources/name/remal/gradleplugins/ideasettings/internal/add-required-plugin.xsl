<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
>

  <xsl:import href="utils.xsl"/>


  <xsl:param name="plugin-id" required="yes"/>


  <xsl:template match="/project">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>

      <xsl:call-template name="ensure-component">
        <xsl:with-param name="component-name" select="'ExternalDependencies'"/>
      </xsl:call-template>
    </xsl:copy>
  </xsl:template>


  <xsl:template match="component[@name = 'ExternalDependencies']">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>

      <xsl:if test="not(plugin[@id = $plugin-id])">
        <plugin id="{$plugin-id}"/>
      </xsl:if>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
