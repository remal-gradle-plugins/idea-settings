<?xml version="1.0" encoding = "UTF-8" standalone="yes"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
>

  <xsl:import href="utils.xsl"/>


  <xsl:param name="default-dialect" required="yes"/>


  <xsl:template match="/project">
    <xsl:call-template name="ensure-project-component">
      <xsl:with-param name="component-name" select="'SqlDialectMappings'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="/project[$default-dialect != 'null']/component[@name = 'SqlDialectMappings']">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>

      <file url="PROJECT" dialect="{$default-dialect}"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="/project[$default-dialect != 'null']/component[@name = 'SqlDialectMappings']/file[@url = 'PROJECT']"/>

</xsl:stylesheet>
