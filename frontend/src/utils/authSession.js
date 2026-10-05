let sessionRevision = 0

export const getAuthSessionRevision = () => sessionRevision

export const advanceAuthSession = () => {
	sessionRevision += 1
	return sessionRevision
}
