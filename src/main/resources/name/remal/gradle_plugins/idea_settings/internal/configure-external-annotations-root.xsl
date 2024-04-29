<?xml version="1.0" encoding = "UTF-8" standalone="yes"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
>

  <xsl:import href="utils.xsl"/>


  <xsl:param name="external-annotations-root" required="yes"/>


  <xsl:template match="/component[@name = 'libraryTable']/library/ANNOTATIONS"/>

  <xsl:template match="/component[@name = 'libraryTable']/library">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
      <ANNOTATIONS>
        <root url="{$external-annotations-root}"/>
      </ANNOTATIONS>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
