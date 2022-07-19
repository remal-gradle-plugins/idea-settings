<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
>

  <xsl:template match="comment()" priority="9999"/>


  <xsl:template match="
      processorPath
    | path
    | *[recent]
    | *[text()[normalize-space(.) != '']]
    | component[@name = 'CheckStyle-IDEA' and @serialisationVersion = '2']
    | component[@name = 'InspectionProjectProfileManager']
    | component[@name = 'FrameworkDetectionExcludesConfiguration']
  " priority="999">
    <xsl:copy-of select="."/>
  </xsl:template>


  <xsl:template match="*">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>

      <xsl:apply-templates select="node()">
        <xsl:sort select="name()"/>
        <xsl:sort select="@id"/>
        <xsl:sort select="@name"/>
        <xsl:sort select="@key"/>
        <xsl:sort select="@index" data-type="number"/>
      </xsl:apply-templates>

    </xsl:copy>
  </xsl:template>


  <xsl:template match="@*|node()" priority="-9999">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
