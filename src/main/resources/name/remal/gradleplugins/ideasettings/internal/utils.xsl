<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
>

  <xsl:template name="ensure-project-component">
    <xsl:param name="component-name" required="yes"/>

    <xsl:choose>
      <xsl:when test="not(component[@name = $component-name])">
        <xsl:variable name="new-document" as="document-node()">
          <xsl:document validation="preserve">
            <xsl:copy>
              <xsl:copy-of select="@*|node()"/>
              <component name="{$component-name}"/>
            </xsl:copy>
          </xsl:document>
        </xsl:variable>
        <xsl:apply-templates select="$new-document" mode="#current"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy>
          <xsl:apply-templates select="@*|node()" mode="#current"/>
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template match="@*|node()" mode="#all" priority="-9999">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
