<?xml version="1.0" encoding = "UTF-8" standalone="yes"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
>

  <xsl:import href="utils.xsl"/>


  <xsl:param name="checkstyle-version" required="yes"/>
  <xsl:param name="treat-errors-as-warnings" required="yes"/>
  <xsl:param name="is-bundled-sun-checks" required="yes"/>
  <xsl:param name="is-bundled-google-checks" required="yes"/>
  <xsl:param name="config-new-id" required="no"/>
  <xsl:param name="config-location-type" required="no"/>
  <xsl:param name="config-location" required="no"/>
  <xsl:param name="config-description" required="no"/>
  <xsl:param name="thirdparty-classpath" required="no"/>


  <!-- #region Version 1 -->

  <xsl:template match="/project/component[@name = 'CheckStyle-IDEA' and not(@serialisationVersion)]">
    <xsl:apply-templates select="." mode="version-1"/>
  </xsl:template>

  <xsl:template match="option[@name = 'configuration']/map" mode="version-1">
    <xsl:copy>
      <xsl:apply-templates select="@*" mode="#current"/>
      <xsl:apply-templates select="*[name() != 'entry']" mode="#current"/>

      <xsl:variable name="entry-nodes">
        <xsl:apply-templates select="entry" mode="#current"/>

        <xsl:if test="$treat-errors-as-warnings != 'null'">
          <xsl:if test="not(entry[@key = 'suppress-errors'])">
            <entry key="suppress-errors" value="{$treat-errors-as-warnings}"/>
          </xsl:if>
        </xsl:if>

        <xsl:choose>
          <xsl:when test="$is-bundled-sun-checks">
            <entry key="active-configuration" value="BUNDLED:(bundled):Sun Checks"/>
          </xsl:when>
          <xsl:when test="$is-bundled-google-checks">
            <entry key="active-configuration" value="BUNDLED:(bundled):Google Checks"/>
          </xsl:when>
          <xsl:when test="$config-location-type and $config-location and $config-description">
            <entry key="active-configuration"
              value="{concat($config-location-type, ':', $config-location, ':', $config-description)}"/>
            <entry key="location-"
              value="{concat($config-location-type, ':', $config-location, ':', $config-description)}"/>
          </xsl:when>
        </xsl:choose>

        <xsl:if test="$thirdparty-classpath">
          <entry key="thirdparty-classpath" value="{string-join($thirdparty-classpath, ';')}"/>
        </xsl:if>
      </xsl:variable>
      <xsl:copy-of select="$entry-nodes/entry[not(starts-with(@key, 'location-'))]"/>
      <xsl:for-each select="$entry-nodes/entry[starts-with(@key, 'location-')]">
        <entry key="location-{position() - 1}" value="{@value}"/>
      </xsl:for-each>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="entry[@key = 'checkstyle-version']" mode="version-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
      <xsl:attribute name="value" select="$checkstyle-version"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="entry[@key = 'suppress-errors' and $treat-errors-as-warnings != 'null']" mode="version-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
      <xsl:attribute name="value" select="$treat-errors-as-warnings"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="entry[@key = 'copy-libs']" mode="version-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
      <xsl:attribute name="value" select="'true'"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="entry[@key = 'scanscope']" mode="version-1">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
      <xsl:choose>
        <xsl:when test="@value = 'JavaOnly'">
          <xsl:attribute name="value" select="'JavaOnlyWithTests'"/>
        </xsl:when>
        <xsl:when test="@value = 'AllSources'">
          <xsl:attribute name="value" select="'AllSourcesWithTests'"/>
        </xsl:when>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="entry[@key = 'active-configuration']" mode="version-1"/>

  <xsl:template match="entry[
    $config-description
    and starts-with(@key, 'location-')
    and (
      starts-with(@value, concat($config-location-type, ':', $config-location, ':'))
      or ends-with(@value, concat(':', $config-description))
    )
  ]" mode="version-1"/>

  <xsl:template match="entry[@key = 'thirdparty-classpath']" mode="version-1"/>

  <!-- #endregion -->


  <!-- #region Version 2 -->

  <xsl:template match="/project/component[@name = 'CheckStyle-IDEA' and @serialisationVersion = '2']">
    <xsl:apply-templates select="." mode="version-2"/>
  </xsl:template>

  <xsl:template match="checkstyleVersion" mode="version-2">
    <xsl:copy>
      <xsl:apply-templates select="@*" mode="#current"/>
      <xsl:value-of select="$checkstyle-version"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="suppressErrors[$treat-errors-as-warnings != 'null']" mode="version-2">
    <xsl:copy>
      <xsl:apply-templates select="@*" mode="#current"/>
      <xsl:value-of select="$treat-errors-as-warnings"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="copyLibs" mode="version-2">
    <xsl:copy>
      <xsl:apply-templates select="@*" mode="#current"/>
      <xsl:value-of select="'true'"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="scanScope" mode="version-2">
    <xsl:copy>
      <xsl:apply-templates select="@*" mode="#current"/>
      <xsl:choose>
        <xsl:when test="normalize-space(.) = 'JavaOnly'">
          <xsl:value-of select="'JavaOnlyWithTests'"/>
        </xsl:when>
        <xsl:when test="normalize-space(.) = 'AllSources'">
          <xsl:value-of select="'AllSourcesWithTests'"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="node()" mode="#current"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="option[@name = 'thirdPartyClasspath']" mode="version-2"/>

  <xsl:template match="option[@name = 'activeLocationIds']" mode="version-2"/>

  <xsl:template match="option[@name = 'locations']/list" mode="version-2">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>

      <xsl:variable name="config-location-node"
        select="ConfigurationLocation[@type != 'BUNDLED' and (normalize-space(.) = $config-location or @description = $config-description)]"/>
      <xsl:variable name="config-location-node-id">
        <xsl:choose>
          <xsl:when test="$config-location-node">
            <xsl:value-of select="$config-location-node/@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$config-new-id"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <ConfigurationLocation
        id="{$config-location-node-id}"
        type="{$config-location-type}"
        scope="All"
        description="{$config-description}"
      >
        <xsl:value-of select="$config-location"/>
      </ConfigurationLocation>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="option[@name = 'locations']
    /list
    /ConfigurationLocation[@type != 'BUNDLED' and (normalize-space(.) = $config-location or @description = $config-description)]
  " mode="version-2"/>

  <xsl:template match="component[@name = 'CheckStyle-IDEA']" mode="version-2">
    <xsl:copy>
      <xsl:apply-templates select="@*" mode="#current"/>
      <xsl:apply-templates select="*[name() != 'option']" mode="#current"/>

      <xsl:if test="$treat-errors-as-warnings != 'null'">
        <xsl:if test="not(suppressErrors)">
          <suppressErrors>
            <xsl:value-of select="$treat-errors-as-warnings"/>
          </suppressErrors>
        </xsl:if>
      </xsl:if>

      <xsl:if test="not(copyLibs)">
        <copyLibs>true</copyLibs>
      </xsl:if>

      <option name="thirdPartyClasspath">
        <xsl:if test="$thirdparty-classpath">
          <xsl:for-each select="$thirdparty-classpath">
            <option value="{.}"/>
          </xsl:for-each>
        </xsl:if>
      </option>

      <option name="activeLocationIds">
        <xsl:choose>
          <xsl:when test="$is-bundled-sun-checks">
            <xsl:variable name="sun-location-id"
              select="option[@name = 'locations']/list/ConfigurationLocation[@type = 'BUNDLED' and @description = 'Sun Checks']/@id"/>
            <option value="{$sun-location-id}"/>
          </xsl:when>
          <xsl:when test="$is-bundled-google-checks">
            <xsl:variable name="google-location-id"
              select="option[@name = 'locations']/list/ConfigurationLocation[@type = 'BUNDLED' and @description = 'Google Checks']/@id"/>
            <option value="{$google-location-id}"/>
          </xsl:when>
          <xsl:when test="$config-location and $config-description">
            <xsl:variable name="config-location-node"
              select="option[@name = 'locations']/list/ConfigurationLocation[@type != 'BUNDLED' and (normalize-space(.) = $config-location or @description = $config-description)]"/>
            <xsl:variable name="config-location-node-id">
              <xsl:choose>
                <xsl:when test="$config-location-node">
                  <xsl:value-of select="$config-location-node/@id"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$config-new-id"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            <option value="{$config-location-node-id}"/>
          </xsl:when>
        </xsl:choose>
      </option>

      <xsl:apply-templates select="option[@name = 'locations']" mode="#current"/>
    </xsl:copy>
  </xsl:template>

  <!-- #endregion -->


  <!-- #region Unknown version -->

  <xsl:template match="/project/component[@name = 'CheckStyle-IDEA']" priority="-10">
    <xsl:message select="concat('IDEA component ', @name, ': Unsupported @serialisationVersion: ', @serialisationVersion)"/>

    <xsl:copy>
      <xsl:apply-templates select="@*|node()" mode="#current"/>
    </xsl:copy>
  </xsl:template>

  <!-- #endregion -->

</xsl:stylesheet>
