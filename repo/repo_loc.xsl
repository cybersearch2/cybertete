<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xsl:output omit-xml-declaration="yes" indent="yes" use-character-maps="cybersearch-web.ent"/>
  <xsl:character-map name="cybersearch-web.ent">
    <xsl:output-character character="&#x005C;" string="&#x002F;"/>
  </xsl:character-map>
  <xsl:param name="repo_loc"/>
  <xsl:param name="control-factory-repo-url"/>
  <xsl:param name="statusbar-repo-url"/>
  <xsl:template match="/">
    <xsl:result-document href="cybertete.root.target"> 
      <xsl:apply-templates select="@*|node()" />
    </xsl:result-document>
  </xsl:template>
  <xsl:template match="@*|node()">
    <xsl:copy>
        <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
  </xsl:template>
  <xsl:template match="/target/locations/location/repository/@location">
    <xsl:choose>
      <xsl:when test="starts-with(., '${project.url}')">
        <xsl:variable name="p2_url" select="concat(substring-before($repo_loc, 'repo/') , substring(.,15) )"/>
        <xsl:attribute name="location"><xsl:value-of select="$p2_url"/></xsl:attribute> 
      </xsl:when>
      <xsl:when test="starts-with(., '${control-factory-repo.url}')">
        <xsl:attribute name="location"><xsl:value-of select="$control-factory-repo-url"/></xsl:attribute> 
     </xsl:when>
      <xsl:when test="starts-with(., '${statusbar-repo.url}')">
        <xsl:attribute name="location"><xsl:value-of select="$statusbar-repo-url"/></xsl:attribute> 
     </xsl:when>
      <xsl:otherwise><xsl:copy/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
