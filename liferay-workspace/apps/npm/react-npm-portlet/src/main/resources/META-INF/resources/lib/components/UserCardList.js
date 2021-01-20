import React from 'react';
import UserCard from './UserCard';
import { useQuery } from '@apollo/react-hooks';
import { gql } from 'apollo-boost';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayAlert from '@clayui/alert';
import {Liferay} from "../App";

export default function UserCardList() {

  let [currentPage, setCurrentPage] = React.useState(1);
  let [portionNumber, setPortionNumber] = React.useState(1);

  const ALL_USERS = gql`
  query{
    userAccounts(page: ${currentPage}, pageSize: ${5}) {
      items {
        name,
        alternateName,
        id,
        image
      }
      page
      pageSize
      totalCount
    }
  }
  `;

  const {loading, error, data} = useQuery(ALL_USERS);

  if (loading) return <ClayLoadingIndicator />;
  if (error) {
    return (
      <ClayAlert displayType="danger" title="Error:">
        An error occured while loading data.
        <div className="mt-2">
          <code>{error.message}</code>
        </div>
      </ClayAlert>
    );
  }

  let {items} = data.userAccounts;
  let {totalCount} = data.userAccounts;
  let {pageSize} = data.userAccounts;
  let {page} = data.userAccounts;

  if (items.length === 0) {
    return (
      <ClayAlert displayType="info" className="text-center">
        {Liferay().Language.get("this-organization-does-not-have-any-users")}
      </ClayAlert>
    );
  }

  const cards = items.map(
      ({name, id, alternateName, image}) => (
          <UserCard
            image={image}
            key={id}
            name={name}
            alternateName={alternateName}
          />
      )
  );

    let pagesCount = Math.ceil(totalCount / pageSize);
    let pages = [];

    for (let i = 1; i <= pagesCount; i++) {
        pages.push(i);
    }

    let portionSize = 3;
    let portionCount = Math.ceil(pagesCount / portionSize);
    let leftPortionPageNumber = (portionNumber - 1) * portionSize + 1;
    let rightPortionPageNumber  = portionNumber * portionSize;

  return <React.Fragment> <div className="row">{cards}</div>

    <hr/>
    <div>
        { portionNumber > 1 && <span className={'icon-angle'} onClick={() => {
            setPortionNumber(portionNumber - 1);
            setCurrentPage(leftPortionPageNumber - portionSize);
        }}>{<svg className="lexicon-icon lexicon-icon-angle-left" focusable="false" role="presentation" viewBox="0 0 512 512">
            <path className="lexicon-icon-outline" d="M114.106 254.607c0.22 6.936 2.972 13.811 8.272 19.11l227.222 227.221c11.026 11.058 28.94 11.058 39.999 0 11.058-11.026 11.058-28.94 0-39.999l-206.333-206.333c0 0 206.333-206.333 206.333-206.333 11.058-11.059 11.058-28.973 0-39.999-11.058-11.059-28.973-11.059-39.999 0l-227.221 227.221c-5.3 5.3-8.052 12.174-8.273 19.111z"></path>
        </svg>}</span>}

        <b>{pages.filter(p => p >= leftPortionPageNumber && p <= rightPortionPageNumber)
            .map((p, idx) => {
                return <span className={p === page
                    ? 'usersPaginationBtns currenPage'
                    :'usersPaginationBtns'}
                             key={idx}
                             onClick={() => setCurrentPage(p)}>{p}</span>
            })}</b>

        {portionCount > portionNumber && <span className={'icon-angle'} onClick={() => {
            setPortionNumber(portionNumber + 1);
            setCurrentPage(rightPortionPageNumber + 1);
        }}>{<svg className="lexicon-icon lexicon-icon-angle-right" focusable="false" role="presentation" viewBox="0 0 512 512">
            <path className="lexicon-icon-outline" d="M396.394 255.607c-0.22-6.936-2.973-13.81-8.272-19.111l-227.221-227.221c-11.026-11.059-28.94-11.059-39.999 0-11.058 11.026-11.058 28.941 0 39.999l206.333 206.333c0 0-206.333 206.333-206.333 206.333-11.058 11.058-11.058 28.973 0 39.999 11.059 11.059 28.972 11.059 39.999 0l227.221-227.221c5.3-5.3 8.053-12.175 8.272-19.111z"></path>
        </svg>}</span> }
    </div>
  </React.Fragment>
}
