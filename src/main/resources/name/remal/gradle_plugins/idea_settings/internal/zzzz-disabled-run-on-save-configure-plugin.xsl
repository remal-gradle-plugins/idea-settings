<?xml version="1.0" encoding = "UTF-8" standalone="yes"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
>

  <xsl:import href="utils.xsl"/>


  <xsl:param name="reformat-mode" required="yes"/>
  <xsl:param name="optimize-imports" required="yes"/>
  <xsl:param name="custom-actions-enabled" required="yes"/>
  <xsl:param name="custom-actions-disabled" required="yes"/>


  <xsl:template match="/project">
    <xsl:call-template name="ensure-project-component">
      <xsl:with-param name="component-name" select="'SaveActionSettings'"/>
    </xsl:call-template>
  </xsl:template>


  <xsl:template match="/project/component[@name = 'SaveActionSettings']">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="SaveActionSettings"/>
      <xsl:if test="not(option[@name = 'actions'])">
        <xsl:variable name="actions">
          <option name="actions"/>
        </xsl:variable>
        <xsl:apply-templates select="$actions" mode="SaveActionSettings"/>
      </xsl:if>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="option[@name = 'actions']//option[
    ($reformat-mode != 'null' and (@value = 'reformat' or @value = 'reformatChangedCode'))
    or ($optimize-imports = 'false' and @value = 'organizeImports')
  ]" mode="SaveActionSettings"/>

  <xsl:template match="option[@name = 'actions']" mode="SaveActionSettings">
    <xsl:copy>
      <xsl:apply-templates select="@*" mode="#current"/>

      <xsl:variable name="actions">
        <xsl:apply-templates select="//option[@value]" mode="#current"/>

        <option value="activate"/>
        <option value="noActionIfCompileErrors"/>

        <xsl:choose>
          <xsl:when test="$reformat-mode = 'WHOLE_FILE'">
            <option value="reformat"/>
          </xsl:when>
          <xsl:when test="$reformat-mode = 'CHANGED_LINES'">
            <option value="reformatChangedCode"/>
          </xsl:when>
        </xsl:choose>

        <xsl:if test="$optimize-imports = 'true'">
          <option value="organizeImports"/>
        </xsl:if>

        <xsl:for-each select="$custom-actions-enabled">
          <option value="{current()}"/>
        </xsl:for-each>
      </xsl:variable>

      <set>
        <xsl:variable name="all-actions-sorted" select="(
          'activate',
          'activateOnShortcut',
          'activateOnBatch',
          'noActionIfCompileErrors',
          'organizeImports',
          'reformat',
          'reformatChangedCode',
          'rearrange',
          'reload',
          'compile',
          'fieldCanBeFinal',
          'localCanBeFinal',
          'localCanBeFinalExceptImplicit',
          'methodMayBeStatic',
          'unqualifiedFieldAccess',
          'unqualifiedMethodAccess',
          'unqualifiedStaticMemberAccess',
          'customUnqualifiedStaticMemberAccess',
          'missingOverrideAnnotation',
          'useBlocks',
          'generateSerialVersionUID',
          'unnecessaryThis',
          'finalPrivateMethod',
          'unnecessaryFinalOnLocalVariableOrParameter',
          'explicitTypeCanBeDiamond',
          'suppressAnnotation',
          'unnecessarySemicolon',
          'singleStatementInBlock',
          'accessCanBeTightened'
        )"/>
        <xsl:for-each select="$all-actions-sorted">
          <xsl:variable name="action" select="normalize-space(current())"/>
          <xsl:if test="$action and $actions/option[@value = $action]">
            <xsl:if test="not(index-of($custom-actions-disabled, $action))">
              <option value="{$action}"/>
            </xsl:if>
          </xsl:if>
        </xsl:for-each>

        <xsl:for-each select="distinct-values($actions/option/@value)">
          <xsl:sort select="current()"/>
          <xsl:variable name="action" select="normalize-space(current())"/>
          <xsl:if test="$action and not(index-of($all-actions-sorted, $action))">
            <xsl:if test="not(index-of($custom-actions-disabled, $action))">
              <option value="{$action}"/>
            </xsl:if>
          </xsl:if>
        </xsl:for-each>
      </set>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
