<?xml version="1.0" encoding = "UTF-8" standalone="yes"?>
<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0"
>

  <xsl:template match="/">
    <!--
    <project version="4">
      <component name="CheckStyle-IDEA">
        <option name="configuration">
          <map>
            <entry key="copy-libs" value="true" />
            <entry key="location-0" value="BUNDLED:(bundled):Sun Checks;All" />
            <entry key="location-1" value="BUNDLED:(bundled):Google Checks;All" />
            <entry key="scan-before-checkin" value="false" />
            <entry key="scanscope" value="JavaOnlyWithTests" />
            <entry key="suppress-errors" value="false" />
          </map>
        </option>
      </component>
    </project>
    -->

    <project version="4">
      <component name="CheckStyle-IDEA" serialisationVersion="2">
        <scanScope>JavaOnlyWithTests</scanScope>
        <suppressErrors>true</suppressErrors>
        <copyLibs>true</copyLibs>
        <option name="locations">
          <list>
            <ConfigurationLocation id="bundled-sun-checks" type="BUNDLED" scope="All" description="Sun Checks">(bundled)</ConfigurationLocation>
            <ConfigurationLocation id="bundled-google-checks" type="BUNDLED" scope="All" description="Google Checks">(bundled)</ConfigurationLocation>
          </list>
        </option>
      </component>
    </project>
  </xsl:template>

</xsl:stylesheet>
