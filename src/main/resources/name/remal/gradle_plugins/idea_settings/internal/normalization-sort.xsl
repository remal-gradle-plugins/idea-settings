<?xml version="1.0" encoding = "UTF-8" standalone="yes"?>
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
    | component[@name = 'FrameworkDetectionExcludesConfiguration']
  " priority="10">
    <xsl:copy-of select="."/>
  </xsl:template>


  <xsl:template match="component[@name = 'InspectionProjectProfileManager']/profile">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>

      <xsl:apply-templates select="node()[
        name() != 'option'
        and name() != 'inspection_tool'
      ]"/>

      <xsl:apply-templates select="option">
        <xsl:sort select="@name"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="inspection_tool">
        <xsl:sort select="@class"/>
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="component[@name = 'InspectionProjectProfileManager']/profile//node()">
    <xsl:copy-of select="."/>
  </xsl:template>


  <xsl:template match="*" priority="-10">
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
