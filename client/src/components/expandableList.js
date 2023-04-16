import React, { useState } from 'react';

export default function ExpandableList({ items }) {
  const [expanded, setExpanded] = useState(false);
  const maxItems = 10;
  const lineHeight = 20;
  const shouldShowButton = items.length > maxItems;
  const maxHeight = expanded ? 'none' : `${lineHeight * maxItems}px`;

  const handleClick = () => {
    setExpanded(!expanded);
  };

  return (
    <div className="list-container">
      <ul className="expandable-list" style={{ maxHeight }}>
        {items.map((item, index) => (
          <li key={index}>{item}</li>
        ))}
      </ul>
      {shouldShowButton && (
        <button className="expand-btn" onClick={handleClick}>
          {expanded ? 'Show less' : 'Show more'}
        </button>
      )}
    </div>
  );
};