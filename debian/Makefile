#
# This Makefile is responsoble for build debian package.
# 4 variables must be provided:
#   HSN2_VER=2.X
#   BUILD_NUMBER=999
#   DEBIAN_DIST=(experimental|2.X)

DEBIAN_DIST=experimental
HSN2_COMPONENT=norm-url

all:    hsn2-norm-url

clean:  hsn2-norm-url-clean

PKG=hsn2-$(HSN2_COMPONENT)_$(HSN2_VER)-$(BUILD_NUMBER)_all
hsn2-norm-url: hsn2-norm-url-clean
		mkdir -p $(PKG)/DEBIAN
		mkdir -p $(PKG)/etc/hsn2/
		mkdir -p $(PKG)/etc/init.d/
		mkdir -p $(PKG)/opt/hsn2/$(HSN2_COMPONENT)
		mkdir -p $(PKG)/var/log/hsn2
		tar -zxf ../target/hsn2-norm-url-*.tar.gz -C $(PKG)/opt/hsn2/$(HSN2_COMPONENT)
		cp url-normalization.initd $(PKG)/etc/init.d/hsn2-$(HSN2_COMPONENT)
		cp control $(PKG)/DEBIAN
		sed -i "s/{VER}/${HSN2_VER}-${BUILD_NUMBER}/" $(PKG)/DEBIAN/control
		sed -i "s/{DEBIAN_DIST}/${DEBIAN_DIST}/" $(PKG)/DEBIAN/control
		fakeroot dpkg -b $(PKG)

hsn2-norm-url-clean:
		rm -rf hsn2-norm-url_*
