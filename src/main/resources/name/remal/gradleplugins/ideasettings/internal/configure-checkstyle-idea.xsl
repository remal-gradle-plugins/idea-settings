<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
>

  <xsl:import href="utils.xsl"/>


  <xsl:param name="checkstyle-version" required="yes"/>
  <xsl:param name="is-bundled-sun-checks" required="yes"/>
  <xsl:param name="is-bundled-google-checks" required="yes"/>
  <xsl:param name="config-new-id" required="no"/>
  <xsl:param name="config-location-type" required="no"/>
  <xsl:param name="config-location" required="no"/>
  <xsl:param name="config-description" required="no"/>
  <xsl:param name="thirdparty-classpath" required="no"/>


  <!-- #region Version 1 -->

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and not(@serialisationVersion)]
    /option[@name = 'configuration']
    /map
    /entry[@key = 'checkstyle-version']
  ">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
      <xsl:attribute name="value" select="$checkstyle-version"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and not(@serialisationVersion)]
    /option[@name = 'configuration']
    /map
    /entry[@key = 'copy-libs']
  ">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
      <xsl:attribute name="value" select="'true'"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and not(@serialisationVersion)]
    /option[@name = 'configuration']
    /map
    /entry[@key = 'scanscope']
  ">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
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


  <xsl:template match="component[@name = 'CheckStyle-IDEA' and not(@serialisationVersion)]
    /option[@name = 'configuration']
    /map
    /entry[@key = 'active-configuration']
  "/>

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and not(@serialisationVersion)]
    /option[@name = 'configuration']
    /map
    /entry[$config-description and starts-with(@key, 'location-') and (starts-with(@value, concat($config-location-type, ':', $config-location, ':')) or ends-with(@value, concat(':', $config-description)))]
  "/>

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and not(@serialisationVersion)]
    /option[@name = 'configuration']
    /map
    /entry[@key = 'thirdparty-classpath']
  "/>

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and not(@serialisationVersion)]
    /option[@name = 'configuration']
    /map
  ">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="*[name() != 'entry']"/>

      <xsl:variable name="entry-nodes">
        <xsl:apply-templates select="entry"/>

        <xsl:choose>
          <xsl:when test="$is-bundled-sun-checks">
            <entry key="active-configuration" value="BUNDLED:(bundled):Sun Checks"/>
          </xsl:when>
          <xsl:when test="$is-bundled-google-checks">
            <entry key="active-configuration" value="BUNDLED:(bundled):Google Checks"/>
          </xsl:when>
          <xsl:when test="$config-location-type and $config-location and $config-description">
            <entry key="active-configuration" value="{concat($config-location-type, ':', $config-location, ':', $config-description)}"/>
            <entry key="location-" value="{concat($config-location-type, ':', $config-location, ':', $config-description)}"/>
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


  <xsl:template match="component[@name = 'CheckStyle-IDEA' and not(@serialisationVersion)]">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- #endregion -->


  <!-- #region Version 2 -->

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and @serialisationVersion = '2']
    /checkstyleVersion
  ">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:value-of select="$checkstyle-version"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and @serialisationVersion = '2']
    /copyLibs
  ">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:value-of select="'true'"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and @serialisationVersion = '2']
    /scanScope
  ">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:choose>
        <xsl:when test="normalize-space(.) = 'JavaOnly'">
          <xsl:value-of select="'JavaOnlyWithTests'"/>
        </xsl:when>
        <xsl:when test="normalize-space(.) = 'AllSources'">
          <xsl:value-of select="'AllSourcesWithTests'"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="node()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and @serialisationVersion = '2']
    /option[@name = 'thirdPartyClasspath']
  "/>

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and @serialisationVersion = '2']
    /option[@name = 'activeLocationIds']
  "/>

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and @serialisationVersion = '2']
    /option[@name = 'locations']
    /list
  ">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>

      <xsl:variable name="config-location-node" select="ConfigurationLocation[@type != 'BUNDLED' and (normalize-space(.) = $config-location or @description = $config-description)]"/>
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

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and @serialisationVersion = '2']
    /option[@name = 'locations']
    /list
    /ConfigurationLocation[@type != 'BUNDLED' and (normalize-space(.) = $config-location or @description = $config-description)]
  "/>

  <xsl:template match="component[@name = 'CheckStyle-IDEA' and @serialisationVersion = '2']">
    <xsl:copy>
      <xsl:apply-templates select="@*"/>
      <xsl:apply-templates select="*[name() != 'option']"/>

      <xsl:if test="$thirdparty-classpath">
        <option name="thirdPartyClasspath">
          <xsl:for-each select="$thirdparty-classpath">
            <option value="{.}"/>
          </xsl:for-each>
        </option>
      </xsl:if>

      <option name="activeLocationIds">
        <xsl:choose>
          <xsl:when test="$is-bundled-sun-checks">
            <xsl:variable name="sun-location-id" select="option[@name = 'locations']/list/ConfigurationLocation[@type = 'BUNDLED' and @description = 'Sun Checks']/@id"/>
            <option value="{$sun-location-id}"/>
          </xsl:when>
          <xsl:when test="$is-bundled-google-checks">
            <xsl:variable name="google-location-id" select="option[@name = 'locations']/list/ConfigurationLocation[@type = 'BUNDLED' and @description = 'Google Checks']/@id"/>
            <option value="{$google-location-id}"/>
          </xsl:when>
          <xsl:when test="$config-location and $config-description">
            <xsl:variable name="config-location-node" select="option[@name = 'locations']/list/ConfigurationLocation[@type != 'BUNDLED' and (normalize-space(.) = $config-location or @description = $config-description)]"/>
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

      <xsl:apply-templates select="option[@name = 'locations']"/>
    </xsl:copy>
  </xsl:template>

  <!-- #endregion -->


  <!-- #region Unknown version -->

  <xsl:template match="component[@name = 'CheckStyle-IDEA']" priority="-10">
    <xsl:message select="concat('IDEA component ', @name, ': Unsupported @serialisationVersion: ', @serialisationVersion)"/>

    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- #endregion -->

</xsl:stylesheet>
