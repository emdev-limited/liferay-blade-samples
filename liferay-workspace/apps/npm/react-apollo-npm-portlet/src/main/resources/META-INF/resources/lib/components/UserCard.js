import React from 'react';
import {ClayCardWithUser} from '@clayui/card';

export default function UserCard({alternateName, name, image}) {
  return (
    <div className="col-md-4">
      <ClayCardWithUser
        description={alternateName}
        href="#"
        name={name}
        spritemap={Liferay.ThemeDisplay.getPathThemeImages()+'/clay/icons.svg'}
        userImageSrc={image}
      />
  </div>
  );
}
