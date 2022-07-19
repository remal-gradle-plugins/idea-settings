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
  " priority="9999">
    <xsl:copy-of select="."/>
  </xsl:template>


  <xsl:template match="*">
    <xsl:variable name="fixed-node">
      <xsl:copy>
        <xsl:apply-templates select="@*"/>

        <xsl:if test="self::list
          and count(item) = count(*)
        ">
          <xsl:attribute name="size" select="count(item)"/>
        </xsl:if>

        <xsl:if test="self::item
          and parent::list
          and count(../item) = count(../*)
        ">
          <xsl:attribute name="index" select="count(preceding-sibling::item)"/>
        </xsl:if>

      </xsl:copy>
    </xsl:variable>
    <xsl:apply-templates select="$fixed-node/*" mode="sort"/>
  </xsl:template>


  <xsl:template match="*" mode="sort">
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


  <xsl:template match="@*|node()" mode="#all" priority="-9999">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
