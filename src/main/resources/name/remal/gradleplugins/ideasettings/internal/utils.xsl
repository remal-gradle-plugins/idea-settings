<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
>

  <xsl:template name="ensure-component">
    <xsl:param name="component-name" required="yes"/>

    <xsl:if test="not(*[local-name() = 'component' and @name = $component-name])">
      <xsl:variable name="component-node">
        <component name="{$component-name}"/>
      </xsl:variable>
      <xsl:apply-templates select="$component-node"/>
    </xsl:if>
  </xsl:template>


  <xsl:template match="@* | node()" priority="-9999">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
