<?xml version="1.0" encoding = "UTF-8" standalone="yes"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
>

  <xsl:template match="*">
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

      <xsl:apply-templates select="node()"/>

    </xsl:copy>
  </xsl:template>


  <xsl:template match="@*|node()" priority="-9999">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
